package edu.cit.capendit.unisell.admin.dto;

public class VendorStatusUpdateRequest {
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}