package edu.cit.capendit.unisell.inventory.model;

import edu.cit.capendit.unisell.platform.model.Platform;
import edu.cit.capendit.unisell.product.model.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "product_platform_inventory", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"product_id", "platform_id"})
})
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @Column(name = "allocated_quantity", nullable = false)
    private Integer allocatedQuantity;

    public Inventory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Platform getPlatform() { return platform; }
    public void setPlatform(Platform platform) { this.platform = platform; }

    public Integer getAllocatedQuantity() { return allocatedQuantity; }
    public void setAllocatedQuantity(Integer allocatedQuantity) { this.allocatedQuantity = allocatedQuantity; }
}