package edu.cit.capendit.unisell.platform.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.cit.capendit.unisell.platform.inventory.model.ProductPlatformInventory;

import java.util.List;
import java.util.Optional;

public interface ProductPlatformInventoryRepository extends JpaRepository<ProductPlatformInventory, Long> {

    List<ProductPlatformInventory> findByProductIdAndProductVendorEmail(Long productId, String vendorEmail);

    Optional<ProductPlatformInventory> findByProductIdAndPlatformIdAndProductVendorEmail(
            Long productId, Long platformId, String vendorEmail);

    @Query("SELECT COALESCE(SUM(i.allocatedQuantity), 0) FROM ProductPlatformInventory i " +
           "WHERE i.product.id = :productId")
    Integer sumAllocatedQuantityByProductId(@Param("productId") Long productId);
}