package edu.cit.capendit.unisell.order.controller;

import edu.cit.capendit.unisell.order.dto.*;
import edu.cit.capendit.unisell.order.model.Order;
import edu.cit.capendit.unisell.order.repository.OrderRepository;
import edu.cit.capendit.unisell.order.service.OrderService;
import edu.cit.capendit.unisell.platform.model.Platform;
import edu.cit.capendit.unisell.platform.repository.PlatformRepository;
import edu.cit.capendit.unisell.product.model.Product;
import edu.cit.capendit.unisell.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(Authentication authentication) {
        String vendorEmail = authentication.getName();
        List<OrderResponse> responses = orderRepository.findAllByVendorEmail(vendorEmail)
                .stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<?> getOrderItems(@PathVariable Long id, Authentication authentication) {
        String vendorEmail = authentication.getName();
        try {
            return ResponseEntity.ok(orderService.getOrderItems(id, vendorEmail));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request, Authentication authentication) {
        String vendorEmail = authentication.getName();

        Platform platform = platformRepository.findByIdAndVendorEmail(request.getPlatformId(), vendorEmail)
                .orElse(null);
        if (platform == null) {
            return ResponseEntity.status(404).body("Platform not found");
        }

        Map<Product, Integer> requestedItems = new HashMap<>();
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findByIdAndVendorEmail(itemReq.getProductId(), vendorEmail)
                    .orElse(null);
            if (product == null) {
                return ResponseEntity.status(404).body("Product not found: id " + itemReq.getProductId());
            }
            requestedItems.put(product, itemReq.getQuantity());
        }

        Order order = new Order();
        order.setPlatform(platform);
        // vendor is set server-side from the authenticated platform owner, never trusted from the request body
        order.setVendor(platform.getVendor());

        try {
            Order created = orderService.createOrder(order, requestedItems, vendorEmail);
            return ResponseEntity.ok(OrderResponse.fromEntity(created));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                           @RequestBody StatusUpdateRequest request,
                                           Authentication authentication) {
        String vendorEmail = authentication.getName();
        try {
            Order updated = orderService.updateStatus(id, request.getStatus(), vendorEmail);
            return ResponseEntity.ok(OrderResponse.fromEntity(updated));
        } catch (IllegalStateException e) {
            return handleError(e);
        }
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long id,
                                                  @RequestBody PaymentStatusUpdateRequest request,
                                                  Authentication authentication) {
        String vendorEmail = authentication.getName();
        try {
            Order updated = orderService.updatePaymentStatus(id, request.getPaymentStatus(), vendorEmail);
            return ResponseEntity.ok(OrderResponse.fromEntity(updated));
        } catch (IllegalStateException e) {
            return handleError(e);
        }
    }

    // Partial or full return. If orderItemIds is null/empty, every item on the order is returned.
    @PutMapping("/{id}/return")
    public ResponseEntity<?> processReturn(@PathVariable Long id,
                                            @RequestBody ReturnRequest request,
                                            Authentication authentication) {
        String vendorEmail = authentication.getName();
        try {
            Order updated = orderService.processReturn(
                    id, request.getOrderItemIds(), request.getReason(), vendorEmail);
            return ResponseEntity.ok(OrderResponse.fromEntity(updated));
        } catch (IllegalStateException e) {
            return handleError(e);
        }
    }

    @PutMapping("/{id}/shipment-status")
    public ResponseEntity<?> updateShipmentStatus(@PathVariable Long id,
                                                   @RequestBody ShipmentStatusUpdateRequest request,
                                                   Authentication authentication) {
        String vendorEmail = authentication.getName();
        try {
            if ("UNCOLLECTED".equalsIgnoreCase(request.getStatus())) {
                Order updated = orderService.markShipmentUncollected(id, vendorEmail);
                return ResponseEntity.ok(OrderResponse.fromEntity(updated));
            }
            // IN_TRANSIT / DELIVERED handling will be filled in once the full shipment feature lands
            return ResponseEntity.badRequest().body("Unsupported shipment status: " + request.getStatus());
        } catch (IllegalStateException e) {
            return handleError(e);
        }
    }

    private ResponseEntity<?> handleError(IllegalStateException e) {
        String msg = e.getMessage();
        if ("Order not found".equals(msg)) {
            return ResponseEntity.status(404).body(msg);
        }
        return ResponseEntity.badRequest().body(msg);
    }
}