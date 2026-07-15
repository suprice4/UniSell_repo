package edu.cit.capendit.unisell.order.dto;

import java.util.List;

public class ReturnRequest {
    // null or empty = full-order return; otherwise only these OrderItem IDs are marked returned
    private List<Long> orderItemIds;
    private String reason;

    public List<Long> getOrderItemIds() { return orderItemIds; }
    public void setOrderItemIds(List<Long> orderItemIds) { this.orderItemIds = orderItemIds; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}