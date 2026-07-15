package edu.cit.capendit.unisell.order.repository;

import edu.cit.capendit.unisell.order.model.ReturnRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnRecordRepository extends JpaRepository<ReturnRecord, Long> {

    List<ReturnRecord> findAllByOrderId(Long orderId);

    List<ReturnRecord> findAllByOrderVendorEmail(String vendorEmail);
}