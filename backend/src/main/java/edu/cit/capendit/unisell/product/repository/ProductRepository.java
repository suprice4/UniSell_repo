package edu.cit.capendit.unisell.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.cit.capendit.unisell.product.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByVendorEmail(String vendorEmail);

    Optional<Product> findByIdAndVendorEmail(Long id, String vendorEmail);

    boolean existsBySkuIgnoreCase(String sku);

    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Product p WHERE p.vendor.email = :vendorEmail")
    Integer sumQuantityByVendorEmail(@Param("vendorEmail") String vendorEmail);
}