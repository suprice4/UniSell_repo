package edu.cit.capendit.unisell.admin.controller;

import edu.cit.capendit.unisell.admin.dto.AdminReturnResponse;
import edu.cit.capendit.unisell.admin.service.AdminReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/returns")
public class AdminReturnController {

    @Autowired
    private AdminReturnService adminReturnService;

    @GetMapping
    public ResponseEntity<List<AdminReturnResponse>> listReturns() {
        return ResponseEntity.ok(adminReturnService.listReturns());
    }
}