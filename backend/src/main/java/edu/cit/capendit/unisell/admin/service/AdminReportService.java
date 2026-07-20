package edu.cit.capendit.unisell.admin.service;

import edu.cit.capendit.unisell.admin.dto.AdminVendorReportResponse;
import edu.cit.capendit.unisell.auth.model.Role;
import edu.cit.capendit.unisell.auth.model.User;
import edu.cit.capendit.unisell.auth.repository.UserRepository;
import edu.cit.capendit.unisell.order.model.PaymentStatus;
import edu.cit.capendit.unisell.order.repository.OrderRepository;
import edu.cit.capendit.unisell.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<AdminVendorReportResponse> generateVendorReport() {
        return userRepository.findAllByRole(Role.VENDOR)
                .stream()
                .map(this::buildReportForVendor)
                .collect(Collectors.toList());
    }

    private AdminVendorReportResponse buildReportForVendor(User vendor) {
        String vendorEmail = vendor.getEmail();

        long totalOrders = orderRepository.countByVendorEmail(vendorEmail);

        Integer totalInventory = productRepository.sumQuantityByVendorEmail(vendorEmail);
        if (totalInventory == null) {
            totalInventory = 0;
        }

        Map<String, Long> paymentStatusBreakdown = new LinkedHashMap<>();
        for (PaymentStatus status : PaymentStatus.values()) {
            paymentStatusBreakdown.put(status.name(), 0L);
        }
        for (Object[] row : orderRepository.countGroupedByPaymentStatusForVendor(vendorEmail)) {
            PaymentStatus status = (PaymentStatus) row[0];
            Long count = (Long) row[1];
            paymentStatusBreakdown.put(status.name(), count);
        }

        return AdminVendorReportResponse.fromVendor(vendor, totalOrders, totalInventory, paymentStatusBreakdown);
    }
}