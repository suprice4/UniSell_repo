package edu.cit.capendit.unisell.platform.service;

import edu.cit.capendit.unisell.auth.model.User;
import edu.cit.capendit.unisell.auth.repository.UserRepository;
import edu.cit.capendit.unisell.core.exception.VendorResourceNotFoundException;
import edu.cit.capendit.unisell.core.validation.NameValidator;
import edu.cit.capendit.unisell.platform.dto.PlatformRequest;
import edu.cit.capendit.unisell.platform.dto.PlatformResponse;
import edu.cit.capendit.unisell.platform.model.Platform;
import edu.cit.capendit.unisell.platform.repository.PlatformRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatformService {

    private final PlatformRepository platformRepository;
    private final UserRepository userRepository;

    public PlatformService(PlatformRepository platformRepository, UserRepository userRepository) {
        this.platformRepository = platformRepository;
        this.userRepository = userRepository;
    }

    public List<PlatformResponse> getMyPlatforms(String vendorEmail) {
        return platformRepository.findByVendorEmail(vendorEmail)
                .stream()
                .map(p -> new PlatformResponse(p.getId(), p.getName()))
                .toList();
    }

    public PlatformResponse createPlatform(PlatformRequest request, String vendorEmail) {
        NameValidator.validateName(request.getName(), "Platform");

        if (platformRepository.existsByNameIgnoreCaseAndVendorEmail(request.getName().trim(), vendorEmail)) {
            throw new IllegalArgumentException("You already have a platform named '" + request.getName().trim() + "'");
        }

        User vendor = userRepository.findByEmail(vendorEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + vendorEmail));

        Platform platform = new Platform();
        platform.setName(request.getName().trim());
        platform.setVendor(vendor);

        Platform saved = platformRepository.save(platform);
        return new PlatformResponse(saved.getId(), saved.getName());
    }

    public PlatformResponse updatePlatform(Long id, PlatformRequest request, String vendorEmail) {
        NameValidator.validateName(request.getName(), "Platform");

        Platform platform = platformRepository.findByIdAndVendorEmail(id, vendorEmail)
                .orElseThrow(() -> new PlatformNotFoundException(id));

        String trimmedName = request.getName().trim();

        if (!trimmedName.equalsIgnoreCase(platform.getName())
            && platformRepository.existsByNameIgnoreCaseAndVendorEmail(trimmedName, vendorEmail)) {
            throw new IllegalArgumentException("You already have a platform named '" + trimmedName + "'");
        }

        platform.setName(trimmedName);
        Platform saved = platformRepository.save(platform);
        return new PlatformResponse(saved.getId(), saved.getName());
    }

    public void deletePlatform(Long id, String vendorEmail) {
        Platform platform = platformRepository.findByIdAndVendorEmail(id, vendorEmail)
                .orElseThrow(() -> new PlatformNotFoundException(id));

        platformRepository.delete(platform);
    }

    public static class PlatformNotFoundException extends VendorResourceNotFoundException {
        public PlatformNotFoundException(Long id) {
            super("Platform", id);
        }
    }
}