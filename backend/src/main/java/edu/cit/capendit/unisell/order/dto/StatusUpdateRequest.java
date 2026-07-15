package edu.cit.capendit.unisell.order.dto;

import edu.cit.capendit.unisell.order.model.OrderStatus;

public class StatusUpdateRequest {
    private OrderStatus status;

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}