package edu.cit.capendit.unisell.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.capendit.unisell.product.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByVendorEmail(String vendorEmail);

    Optional<Product> findByIdAndVendorEmail(Long id, String vendorEmail);

    boolean existsBySkuIgnoreCase(String sku);
}