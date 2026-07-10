package edu.cit.capendit.unisell.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByVendorEmail(String vendorEmail);

    Optional<Product> findByIdAndVendorEmail(Long id, String vendorEmail);

    boolean existsBySkuIgnoreCase(String sku);
}