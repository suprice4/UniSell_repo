package edu.cit.capendit.unisell.platform;

public class ProductPlatformInventoryResponse {

    private Long id;
    private Long platformId;
    private String platformName;
    private Long productId;
    private Integer allocatedQuantity;

    public ProductPlatformInventoryResponse() {}

    public ProductPlatformInventoryResponse(Long id, Long platformId, String platformName,
                                             Long productId, Integer allocatedQuantity) {
        this.id = id;
        this.platformId = platformId;
        this.platformName = platformName;
        this.productId = productId;
        this.allocatedQuantity = allocatedQuantity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPlatformId() { return platformId; }
    public void setPlatformId(Long platformId) { this.platformId = platformId; }

    public String getPlatformName() { return platformName; }
    public void setPlatformName(String platformName) { this.platformName = platformName; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getAllocatedQuantity() { return allocatedQuantity; }
    public void setAllocatedQuantity(Integer allocatedQuantity) { this.allocatedQuantity = allocatedQuantity; }
}