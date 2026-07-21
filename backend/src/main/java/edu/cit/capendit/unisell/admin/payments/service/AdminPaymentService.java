package edu.cit.capendit.unisell.admin.payments.service;

import edu.cit.capendit.unisell.admin.payments.dto.AdminPaymentResponse;
import edu.cit.capendit.unisell.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminPaymentService {

    @Autowired
    private OrderRepository orderRepository;

    public List<AdminPaymentResponse> listPayments() {
        return orderRepository.findAll()
                .stream()
                .map(AdminPaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
