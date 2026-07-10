package edu.cit.capendit.unisell.platform;

import edu.cit.capendit.unisell.auth.User;
import edu.cit.capendit.unisell.auth.UserRepository;
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
        validateName(request.getName());

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
        validateName(request.getName());

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

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Platform name cannot be empty");
        }
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("Platform name cannot exceed 100 characters");
        }
    }

    public static class PlatformNotFoundException extends RuntimeException {
        public PlatformNotFoundException(Long id) {
            super("Platform not found with id: " + id);
        }
    }
}