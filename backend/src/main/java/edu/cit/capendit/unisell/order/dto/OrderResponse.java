package edu.cit.capendit.unisell.order.dto;

import edu.cit.capendit.unisell.order.model.Order;
import edu.cit.capendit.unisell.order.model.OrderStatus;
import edu.cit.capendit.unisell.order.model.PaymentStatus;
import edu.cit.capendit.unisell.order.model.ShipmentStatus;

import java.time.LocalDateTime;

public class OrderResponse {
    private Long id;
    private Long vendorId;
    private Long platformId;
    private String platformName;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private ShipmentStatus shipmentStatus;
    private Double totalAmount;
    private LocalDateTime createdAt;

    public OrderResponse() {}

    // Maps a raw Order entity to a safe response shape.
    // Never expose Order.vendor or Order.platform directly (they carry the full User entity, including password hash).
    public static OrderResponse fromEntity(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.id = order.getId();
        dto.vendorId = order.getVendor().getId();
        dto.platformId = order.getPlatform().getId();
        dto.platformName = order.getPlatform().getName();
        dto.status = order.getStatus();
        dto.paymentStatus = order.getPaymentStatus();
        dto.shipmentStatus = order.getShipmentStatus();
        dto.totalAmount = order.getTotalAmount();
        dto.createdAt = order.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public Long getPlatformId() { return platformId; }
    public void setPlatformId(Long platformId) { this.platformId = platformId; }

    public String getPlatformName() { return platformName; }
    public void setPlatformName(String platformName) { this.platformName = platformName; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public ShipmentStatus getShipmentStatus() { return shipmentStatus; }
    public void setShipmentStatus(ShipmentStatus shipmentStatus) { this.shipmentStatus = shipmentStatus; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}