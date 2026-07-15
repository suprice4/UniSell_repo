package edu.cit.capendit.unisell.order.dto;

public class ShipmentStatusUpdateRequest {
    private String status; // expects "UNCOLLECTED", "IN_TRANSIT", "DELIVERED"

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}