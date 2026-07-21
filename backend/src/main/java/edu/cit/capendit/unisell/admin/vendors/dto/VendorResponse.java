package edu.cit.capendit.unisell.admin.vendors.dto;

import edu.cit.capendit.unisell.auth.model.User;

public class VendorResponse {
    private Long id;
    private String name;
    private String email;
    private boolean enabled;

    public static VendorResponse fromEntity(User user) {
        VendorResponse dto = new VendorResponse();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.email = user.getEmail();
        dto.enabled = user.isEnabled();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
