package edu.cit.capendit.unisell.platform;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlatformRepository extends JpaRepository<Platform, Long> {

    List<Platform> findByVendorEmail(String vendorEmail);

    Optional<Platform> findByIdAndVendorEmail(Long id, String vendorEmail);

    boolean existsByNameIgnoreCaseAndVendorEmail(String name, String vendorEmail);
}