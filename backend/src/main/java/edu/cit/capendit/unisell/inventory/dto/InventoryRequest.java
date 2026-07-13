package edu.cit.capendit.unisell.inventory.dto;

public class InventoryRequest {

    private Long platformId;
    private Integer allocatedQuantity;

    public InventoryRequest() {}

    public Long getPlatformId() { return platformId; }
    public void setPlatformId(Long platformId) { this.platformId = platformId; }

    public Integer getAllocatedQuantity() { return allocatedQuantity; }
    public void setAllocatedQuantity(Integer allocatedQuantity) { this.allocatedQuantity = allocatedQuantity; }
}