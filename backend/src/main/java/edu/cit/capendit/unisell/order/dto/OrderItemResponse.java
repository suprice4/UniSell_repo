package edu.cit.capendit.unisell.order.dto;

public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double priceAtTimeOfOrder;

    public OrderItemResponse() {}

    public OrderItemResponse(Long id, Long productId, String productName, Integer quantity, Double priceAtTimeOfOrder) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.priceAtTimeOfOrder = priceAtTimeOfOrder;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPriceAtTimeOfOrder() { return priceAtTimeOfOrder; }
    public void setPriceAtTimeOfOrder(Double priceAtTimeOfOrder) { this.priceAtTimeOfOrder = priceAtTimeOfOrder; }
}