package edu.cit.capendit.unisell.admin.vendors.service;

import edu.cit.capendit.unisell.admin.activitylog.model.ActivityActionType;
import edu.cit.capendit.unisell.admin.activitylog.service.ActivityLogService;
import edu.cit.capendit.unisell.admin.vendors.dto.VendorResponse;
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

    @Autowired
    private ActivityLogService activityLogService;

    public List<VendorResponse> listVendors() {
        return userRepository.findAllByRole(Role.VENDOR)
                .stream()
                .map(VendorResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public VendorResponse setEnabled(Long vendorId, boolean enabled, String adminEmail) {
        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new IllegalStateException("Vendor not found"));

        if (user.getRole() != Role.VENDOR) {
            throw new IllegalStateException("Only vendor accounts can be activated or deactivated");
        }

        user.setEnabled(enabled);
        userRepository.save(user);

        activityLogService.log(adminEmail, "ADMIN",
                enabled ? ActivityActionType.VENDOR_ACCOUNT_ENABLED : ActivityActionType.VENDOR_ACCOUNT_DISABLED,
                "Vendor account " + (enabled ? "activated" : "deactivated") + ": " + user.getEmail(),
                "USER", user.getId());

        return VendorResponse.fromEntity(user);
    }
}
