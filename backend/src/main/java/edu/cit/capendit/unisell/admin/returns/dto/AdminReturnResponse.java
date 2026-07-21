package edu.cit.capendit.unisell.admin.returns.dto;

import edu.cit.capendit.unisell.order.model.ReturnRecord;

import java.time.LocalDateTime;

public class AdminReturnResponse {

    private Long id;
    private Long orderId;
    private String vendorName;
    private String vendorEmail;
    private String productName;
    private Integer quantity;
    private String reason;
    private LocalDateTime returnedAt;

    public static AdminReturnResponse fromEntity(ReturnRecord returnRecord) {
        AdminReturnResponse dto = new AdminReturnResponse();
        dto.id = returnRecord.getId();
        dto.orderId = returnRecord.getOrder().getId();
        dto.vendorName = returnRecord.getOrder().getVendor().getName();
        dto.vendorEmail = returnRecord.getOrder().getVendor().getEmail();
        dto.productName = returnRecord.getOrderItem().getProduct().getName();
        dto.quantity = returnRecord.getOrderItem().getQuantity();
        dto.reason = returnRecord.getReason();
        dto.returnedAt = returnRecord.getReturnedAt();
        return dto;
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public String getVendorName() { return vendorName; }
    public String getVendorEmail() { return vendorEmail; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
    public String getReason() { return reason; }
    public LocalDateTime getReturnedAt() { return returnedAt; }
}
