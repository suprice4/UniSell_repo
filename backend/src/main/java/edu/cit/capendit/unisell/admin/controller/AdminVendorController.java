package edu.cit.capendit.unisell.admin.controller;

import edu.cit.capendit.unisell.admin.dto.VendorResponse;
import edu.cit.capendit.unisell.admin.dto.VendorStatusUpdateRequest;
import edu.cit.capendit.unisell.admin.service.AdminVendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/vendors")
public class AdminVendorController {

    @Autowired
    private AdminVendorService adminVendorService;

    @GetMapping
    public ResponseEntity<List<VendorResponse>> listVendors() {
        return ResponseEntity.ok(adminVendorService.listVendors());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                           @RequestBody VendorStatusUpdateRequest request) {
        try {
            return ResponseEntity.ok(adminVendorService.setEnabled(id, request.isEnabled()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}