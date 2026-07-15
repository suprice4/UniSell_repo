package edu.cit.capendit.unisell.order.dto;

import java.util.List;

public class OrderRequest {
    private Long platformId;
    private List<OrderItemRequest> items;

    public Long getPlatformId() { return platformId; }
    public void setPlatformId(Long platformId) { this.platformId = platformId; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}