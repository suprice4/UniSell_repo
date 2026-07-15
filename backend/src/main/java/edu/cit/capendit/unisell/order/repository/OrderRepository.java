package edu.cit.capendit.unisell.order.repository;

import edu.cit.capendit.unisell.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByVendorEmail(String vendorEmail);

    Optional<Order> findByIdAndVendorEmail(Long id, String vendorEmail);
}