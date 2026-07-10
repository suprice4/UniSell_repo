package edu.cit.capendit.unisell.product;

public class ProductResponse {

    private Long id;
    private String name;
    private String sku;
    private Double price;
    private Integer quantity;
    private Integer lowStockThreshold;
    private Long categoryId;
    private String categoryName;

    public ProductResponse() {}

    public ProductResponse(Long id, String name, String sku, Double price, Integer quantity,
                            Integer lowStockThreshold, Long categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(Integer lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}