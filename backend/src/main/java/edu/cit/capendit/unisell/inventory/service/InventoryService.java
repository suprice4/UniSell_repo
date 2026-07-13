package edu.cit.capendit.unisell.inventory.service;

import edu.cit.capendit.unisell.inventory.dto.InventoryRequest;
import edu.cit.capendit.unisell.inventory.dto.InventoryResponse;
import edu.cit.capendit.unisell.inventory.model.Inventory;
import edu.cit.capendit.unisell.inventory.repository.InventoryRepository;
import edu.cit.capendit.unisell.platform.model.Platform;
import edu.cit.capendit.unisell.platform.repository.PlatformRepository;
import edu.cit.capendit.unisell.product.model.Product;
import edu.cit.capendit.unisell.product.repository.ProductRepository;
import edu.cit.capendit.unisell.product.service.ProductService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final PlatformRepository platformRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                                            ProductRepository productRepository,
                                            PlatformRepository platformRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.platformRepository = platformRepository;
    }

    public List<InventoryResponse> getAllocationsForProduct(Long productId, String vendorEmail) {
        // Confirm the product belongs to the caller first
        productRepository.findByIdAndVendorEmail(productId, vendorEmail)
                .orElseThrow(() -> new ProductService.ProductNotFoundException(productId));

        return inventoryRepository.findByProductIdAndProductVendorEmail(productId, vendorEmail)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public InventoryResponse allocateStock(Long productId,
                                                            InventoryRequest request,
                                                            String vendorEmail) {
        Product product = productRepository.findByIdAndVendorEmail(productId, vendorEmail)
                .orElseThrow(() -> new ProductService.ProductNotFoundException(productId));

        validateQuantity(request.getAllocatedQuantity());

        if (request.getPlatformId() == null) {
            throw new IllegalArgumentException("Platform must be specified");
        }

        Platform platform = platformRepository.findByIdAndVendorEmail(request.getPlatformId(), vendorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Platform not found or does not belong to you"));

        // Reject-duplicate: if an allocation for this product+platform already exists, require PUT instead
        inventoryRepository.findByProductIdAndPlatformIdAndProductVendorEmail(
                        productId, platform.getId(), vendorEmail)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "An allocation for this product on '" + platform.getName() +
                            "' already exists. Use update instead.");
                });

        int existingSum = inventoryRepository.sumAllocatedQuantityByProductId(productId);
        int newTotal = existingSum + request.getAllocatedQuantity();

        if (newTotal > product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Allocation exceeds available stock: product has " + product.getQuantity() +
                    " total, " + existingSum + " already allocated, cannot allocate " +
                    request.getAllocatedQuantity() + " more.");
        }

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setPlatform(platform);
        inventory.setAllocatedQuantity(request.getAllocatedQuantity());

        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }

    public InventoryResponse updateAllocation(Long productId, Long platformId,
                                                               InventoryRequest request,
                                                               String vendorEmail) {
        Product product = productRepository.findByIdAndVendorEmail(productId, vendorEmail)
                .orElseThrow(() -> new ProductService.ProductNotFoundException(productId));

        validateQuantity(request.getAllocatedQuantity());

        Inventory inventory = inventoryRepository
                .findByProductIdAndPlatformIdAndProductVendorEmail(productId, platformId, vendorEmail)
                .orElseThrow(() -> new InventoryNotFoundException(productId, platformId));

        int existingSum = inventoryRepository.sumAllocatedQuantityByProductId(productId);
        int sumExcludingThis = existingSum - inventory.getAllocatedQuantity();
        int newTotal = sumExcludingThis + request.getAllocatedQuantity();

        if (newTotal > product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Allocation exceeds available stock: product has " + product.getQuantity() +
                    " total, " + sumExcludingThis + " allocated elsewhere, cannot set this allocation to " +
                    request.getAllocatedQuantity() + ".");
        }

        inventory.setAllocatedQuantity(request.getAllocatedQuantity());
        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }

    public void deleteAllocation(Long productId, Long platformId, String vendorEmail) {
        productRepository.findByIdAndVendorEmail(productId, vendorEmail)
                .orElseThrow(() -> new ProductService.ProductNotFoundException(productId));

        Inventory inventory = inventoryRepository
                .findByProductIdAndPlatformIdAndProductVendorEmail(productId, platformId, vendorEmail)
                .orElseThrow(() -> new InventoryNotFoundException(productId, platformId));

        inventoryRepository.delete(inventory);
    }

    private void validateQuantity(Integer allocatedQuantity) {
        if (allocatedQuantity == null || allocatedQuantity < 0) {
            throw new IllegalArgumentException("Allocated quantity cannot be null or negative");
        }
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getPlatform().getId(),
                inventory.getPlatform().getName(),
                inventory.getProduct().getId(),
                inventory.getAllocatedQuantity()
        );
    }

    public static class InventoryNotFoundException extends RuntimeException {
        public InventoryNotFoundException(Long productId, Long platformId) {
            super("No allocation found for product " + productId + " on platform " + platformId);
        }
    }
}