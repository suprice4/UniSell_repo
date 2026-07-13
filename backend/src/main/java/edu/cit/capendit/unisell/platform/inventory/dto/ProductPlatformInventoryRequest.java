package edu.cit.capendit.unisell.platform.inventory.dto;

public class ProductPlatformInventoryRequest {

    private Long platformId;
    private Integer allocatedQuantity;

    public ProductPlatformInventoryRequest() {}

    public Long getPlatformId() { return platformId; }
    public void setPlatformId(Long platformId) { this.platformId = platformId; }

    public Integer getAllocatedQuantity() { return allocatedQuantity; }
    public void setAllocatedQuantity(Integer allocatedQuantity) { this.allocatedQuantity = allocatedQuantity; }
}