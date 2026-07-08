package edu.cit.capendit.unisell.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByVendorEmail(String vendorEmail);

    Optional<Category> findByIdAndVendorEmail(Long id, String vendorEmail);

    boolean existsByNameIgnoreCaseAndVendorEmail(String name, String vendorEmail);
}