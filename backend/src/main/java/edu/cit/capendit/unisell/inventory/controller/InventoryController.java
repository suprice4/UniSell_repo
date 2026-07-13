package edu.cit.capendit.unisell.inventory.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import edu.cit.capendit.unisell.inventory.dto.InventoryRequest;
import edu.cit.capendit.unisell.inventory.dto.InventoryResponse;
import edu.cit.capendit.unisell.inventory.service.InventoryService;
import edu.cit.capendit.unisell.inventory.service.InventoryService.InventoryNotFoundException;
import edu.cit.capendit.unisell.product.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<?> getAllocations(@PathVariable Long productId, Authentication authentication) {
        try {
            List<InventoryResponse> response =
                    inventoryService.getAllocationsForProduct(productId, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (ProductService.ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> allocateStock(@PathVariable Long productId,
                                            @RequestBody InventoryRequest request,
                                            Authentication authentication) {
        try {
            InventoryResponse response =
                    inventoryService.allocateStock(productId, request, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ProductService.ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{platformId}")
    public ResponseEntity<?> updateAllocation(@PathVariable Long productId,
                                               @PathVariable Long platformId,
                                               @RequestBody InventoryRequest request,
                                               Authentication authentication) {
        try {
            InventoryResponse response =
                    inventoryService.updateAllocation(productId, platformId, request, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (ProductService.ProductNotFoundException | InventoryService.InventoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{platformId}")
    public ResponseEntity<?> deleteAllocation(@PathVariable Long productId,
                                               @PathVariable Long platformId,
                                               Authentication authentication) {
        try {
            inventoryService.deleteAllocation(productId, platformId, authentication.getName());
            return ResponseEntity.noContent().build();
        } catch (ProductService.ProductNotFoundException | InventoryService.InventoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}