package edu.cit.capendit.unisell.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.cit.capendit.unisell.inventory.model.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByProductIdAndProductVendorEmail(Long productId, String vendorEmail);

    Optional<Inventory> findByProductIdAndPlatformIdAndProductVendorEmail(
            Long productId, Long platformId, String vendorEmail);

    @Query("SELECT COALESCE(SUM(i.allocatedQuantity), 0) FROM Inventory i " +
           "WHERE i.product.id = :productId")
    Integer sumAllocatedQuantityByProductId(@Param("productId") Long productId);
}