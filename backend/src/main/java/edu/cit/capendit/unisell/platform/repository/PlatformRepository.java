package edu.cit.capendit.unisell.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.capendit.unisell.platform.model.Platform;

import java.util.List;
import java.util.Optional;

public interface PlatformRepository extends JpaRepository<Platform, Long> {

    List<Platform> findByVendorEmail(String vendorEmail);

    Optional<Platform> findByIdAndVendorEmail(Long id, String vendorEmail);

    boolean existsByNameIgnoreCaseAndVendorEmail(String name, String vendorEmail);
}