package edu.cit.capendit.unisell.admin.controller;

import edu.cit.capendit.unisell.admin.dto.AdminPaymentResponse;
import edu.cit.capendit.unisell.admin.service.AdminPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
public class AdminPaymentController {

    @Autowired
    private AdminPaymentService adminPaymentService;

    @GetMapping
    public ResponseEntity<List<AdminPaymentResponse>> listPayments() {
        return ResponseEntity.ok(adminPaymentService.listPayments());
    }
}