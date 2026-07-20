package edu.cit.capendit.unisell.order.repository;

import edu.cit.capendit.unisell.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByVendorEmail(String vendorEmail);

    Optional<Order> findByIdAndVendorEmail(Long id, String vendorEmail);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.vendor.email = :vendorEmail")
    long countByVendorEmail(@Param("vendorEmail") String vendorEmail);

    @Query("SELECT o.paymentStatus, COUNT(o) FROM Order o WHERE o.vendor.email = :vendorEmail GROUP BY o.paymentStatus")
    List<Object[]> countGroupedByPaymentStatusForVendor(@Param("vendorEmail") String vendorEmail);
}