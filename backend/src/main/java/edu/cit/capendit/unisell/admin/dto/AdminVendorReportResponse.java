package edu.cit.capendit.unisell.admin.dto;

import edu.cit.capendit.unisell.auth.model.User;

import java.util.Map;

public class AdminVendorReportResponse {

    private Long vendorId;
    private String vendorName;
    private String vendorEmail;
    private long totalOrders;
    private int totalInventory;
    private Map<String, Long> paymentStatusBreakdown;

    public AdminVendorReportResponse() {}

    public static AdminVendorReportResponse fromVendor(
            User vendor,
            long totalOrders,
            int totalInventory,
            Map<String, Long> paymentStatusBreakdown
    ) {
        AdminVendorReportResponse dto = new AdminVendorReportResponse();
        dto.vendorId = vendor.getId();
        dto.vendorName = vendor.getName();
        dto.vendorEmail = vendor.getEmail();
        dto.totalOrders = totalOrders;
        dto.totalInventory = totalInventory;
        dto.paymentStatusBreakdown = paymentStatusBreakdown;
        return dto;
    }

    public Long getVendorId() { return vendorId; }
    public String getVendorName() { return vendorName; }
    public String getVendorEmail() { return vendorEmail; }
    public long getTotalOrders() { return totalOrders; }
    public int getTotalInventory() { return totalInventory; }
    public Map<String, Long> getPaymentStatusBreakdown() { return paymentStatusBreakdown; }
}