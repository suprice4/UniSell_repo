package edu.cit.capendit.unisell.admin.controller;

import edu.cit.capendit.unisell.admin.dto.AdminVendorReportResponse;
import edu.cit.capendit.unisell.admin.service.AdminReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
public class AdminReportController {

    @Autowired
    private AdminReportService adminReportService;

    @GetMapping
    public ResponseEntity<List<AdminVendorReportResponse>> getVendorReport() {
        return ResponseEntity.ok(adminReportService.generateVendorReport());
    }
}