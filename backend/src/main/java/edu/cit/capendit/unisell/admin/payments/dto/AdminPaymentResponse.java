package edu.cit.capendit.unisell.admin.payments.dto;

import edu.cit.capendit.unisell.order.model.Order;

import java.time.LocalDateTime;

public class AdminPaymentResponse {

    private Long orderId;
    private String vendorName;
    private String vendorEmail;
    private String platformName;
    private Double totalAmount;
    private String paymentStatus;
    private LocalDateTime createdAt;

    public static AdminPaymentResponse fromEntity(Order order) {
        AdminPaymentResponse dto = new AdminPaymentResponse();
        dto.orderId = order.getId();
        dto.vendorName = order.getVendor().getName();
        dto.vendorEmail = order.getVendor().getEmail();
        dto.platformName = order.getPlatform().getName();
        dto.totalAmount = order.getTotalAmount();
        dto.paymentStatus = order.getPaymentStatus().name();
        dto.createdAt = order.getCreatedAt();
        return dto;
    }

    public Long getOrderId() { return orderId; }
    public String getVendorName() { return vendorName; }
    public String getVendorEmail() { return vendorEmail; }
    public String getPlatformName() { return platformName; }
    public Double getTotalAmount() { return totalAmount; }
    public String getPaymentStatus() { return paymentStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
