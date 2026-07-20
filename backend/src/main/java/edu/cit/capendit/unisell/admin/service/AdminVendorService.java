package edu.cit.capendit.unisell.admin.service;

import edu.cit.capendit.unisell.admin.dto.VendorResponse;
import edu.cit.capendit.unisell.auth.model.Role;
import edu.cit.capendit.unisell.auth.model.User;
import edu.cit.capendit.unisell.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminVendorService {

    @Autowired
    private UserRepository userRepository;

    public List<VendorResponse> listVendors() {
        return userRepository.findAllByRole(Role.VENDOR)
                .stream()
                .map(VendorResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public VendorResponse setEnabled(Long vendorId, boolean enabled) {
        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new IllegalStateException("Vendor not found"));

        if (user.getRole() != Role.VENDOR) {
            throw new IllegalStateException("Only vendor accounts can be activated or deactivated");
        }

        user.setEnabled(enabled);
        userRepository.save(user);
        return VendorResponse.fromEntity(user);
    }
}