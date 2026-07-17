package edu.cit.capendit.unisell.order.service;

import edu.cit.capendit.unisell.inventory.model.Inventory;
import edu.cit.capendit.unisell.inventory.repository.InventoryRepository;
import edu.cit.capendit.unisell.order.model.*;
import edu.cit.capendit.unisell.order.repository.OrderItemRepository;
import edu.cit.capendit.unisell.order.repository.OrderRepository;
import edu.cit.capendit.unisell.order.repository.ReturnRecordRepository;
import edu.cit.capendit.unisell.product.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ReturnRecordRepository returnRecordRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    // requestedItems: product -> quantity
    @Transactional
    public Order createOrder(Order order, Map<Product, Integer> requestedItems, String vendorEmail) {

        double total = 0.0;

        for (Map.Entry<Product, Integer> entry : requestedItems.entrySet()) {
            Product product = entry.getKey();
            Integer requestedQty = entry.getValue();

            Inventory inventory = inventoryRepository
                    .findByProductIdAndPlatformIdAndProductVendorEmail(
                            product.getId(), order.getPlatform().getId(), vendorEmail)
                    .orElseThrow(() -> new IllegalStateException(
                            "No inventory allocation found for product '" + product.getName()
                                    + "' on platform '" + order.getPlatform().getName() + "'"));

            if (requestedQty > inventory.getAllocatedQuantity()) {
                throw new IllegalStateException(
                        "Requested quantity (" + requestedQty + ") for '" + product.getName()
                                + "' exceeds allocated stock (" + inventory.getAllocatedQuantity()
                                + ") on platform '" + order.getPlatform().getName() + "'");
            }

            // decrement allocation so the same stock can't be double-sold
            inventory.setAllocatedQuantity(inventory.getAllocatedQuantity() - requestedQty);
            inventoryRepository.save(inventory);

            total += product.getPrice() * requestedQty;
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        for (Map.Entry<Product, Integer> entry : requestedItems.entrySet()) {
            Product product = entry.getKey();
            Integer requestedQty = entry.getValue();

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(product);
            item.setQuantity(requestedQty);
            item.setPriceAtTimeOfOrder(product.getPrice());
            orderItemRepository.save(item);
        }

        return savedOrder;
    }

    public List<edu.cit.capendit.unisell.order.dto.OrderItemResponse> getOrderItems(Long orderId, String vendorEmail) {
        orderRepository.findByIdAndVendorEmail(orderId, vendorEmail)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        List<OrderItem> items = orderItemRepository.findAllByOrderId(orderId);
        return items.stream()
                .map(item -> new edu.cit.capendit.unisell.order.dto.OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPriceAtTimeOfOrder()
                ))
                .toList();
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus newStatus, String vendorEmail) {
        Order order = orderRepository.findByIdAndVendorEmail(orderId, vendorEmail)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        validateStatusTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);

        // Full-order RETURNED via generic status update covers all items.
        // For partial returns, use processReturn() instead.
        if (newStatus == OrderStatus.RETURNED) {
            generateReturnRecords(order, null, null);
        }

        return orderRepository.save(order);
    }

    @Transactional
    public Order updatePaymentStatus(Long orderId, PaymentStatus newStatus, String vendorEmail) {
        Order order = orderRepository.findByIdAndVendorEmail(orderId, vendorEmail)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        validatePaymentTransition(order.getPaymentStatus(), newStatus);
        order.setPaymentStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional
    public Order processReturn(Long orderId, List<Long> orderItemIds, String reason, String vendorEmail) {
        Order order = orderRepository.findByIdAndVendorEmail(orderId, vendorEmail)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        // A return (partial or full) moves the order into RETURNED status.
        validateStatusTransition(order.getStatus(), OrderStatus.RETURNED);
        order.setStatus(OrderStatus.RETURNED);
        orderRepository.save(order);

        generateReturnRecords(order, orderItemIds, reason);
        return order;
    }

    @Transactional
    public Order markShipmentUncollected(Long orderId, String vendorEmail) {
        Order order = orderRepository.findByIdAndVendorEmail(orderId, vendorEmail)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        order.setShipmentStatus(ShipmentStatus.UNCOLLECTED);

        // Per SRS: uncollected shipment triggers a return, so status must also move to RETURNED
        validateStatusTransition(order.getStatus(), OrderStatus.RETURNED);
        order.setStatus(OrderStatus.RETURNED);
        orderRepository.save(order);

        generateReturnRecords(order, null, "Shipment uncollected");
        return order;
    }

    @Transactional
    public Order updateShipmentDetails(Long orderId, String trackingNumber, String courierName, String vendorEmail) {
        Order order = orderRepository.findByIdAndVendorEmail(orderId, vendorEmail)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Shipment details can only be set for orders with status SHIPPED");
        }

        order.setTrackingNumber(trackingNumber);
        order.setCourierName(courierName);
        return orderRepository.save(order);
    }

    // orderItemIds: null/empty = every item on the order gets a ReturnRecord (full return).
    // Non-empty = only the specified OrderItem IDs are marked returned (partial return).
    @Transactional
    public void generateReturnRecords(Order order, List<Long> orderItemIds, String reason) {
        List<OrderItem> items = orderItemRepository.findAllByOrderId(order.getId());

        for (OrderItem item : items) {
            if (orderItemIds == null || orderItemIds.isEmpty() || orderItemIds.contains(item.getId())) {
                ReturnRecord record = new ReturnRecord();
                record.setOrder(order);
                record.setOrderItem(item);
                record.setReason(reason);
                returnRecordRepository.save(record);
            }
        }
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == OrderStatus.PROCESSING || next == OrderStatus.RETURNED;
            case PROCESSING -> next == OrderStatus.SHIPPED || next == OrderStatus.RETURNED;
            case SHIPPED -> next == OrderStatus.DELIVERED || next == OrderStatus.RETURNED;
            case DELIVERED -> next == OrderStatus.RETURNED;
            case RETURNED -> false;
        };

        if (!valid) {
            throw new IllegalStateException(
                    "Invalid status transition: " + current + " -> " + next);
        }
    }

    private void validatePaymentTransition(PaymentStatus current, PaymentStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == PaymentStatus.RECEIVED;
            case RECEIVED -> next == PaymentStatus.REFUNDED;
            case REFUNDED -> false;
        };

        if (!valid) {
            throw new IllegalStateException(
                    "Invalid payment status transition: " + current + " -> " + next);
        }
    }
}