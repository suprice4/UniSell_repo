package edu.cit.capendit.unisell.product.service;

import edu.cit.capendit.unisell.auth.model.User;
import edu.cit.capendit.unisell.auth.repository.UserRepository;
import edu.cit.capendit.unisell.category.model.Category;
import edu.cit.capendit.unisell.category.repository.CategoryRepository;
import edu.cit.capendit.unisell.product.model.Product;
import edu.cit.capendit.unisell.product.repository.ProductRepository;
import edu.cit.capendit.unisell.product.dto.ProductRequest;
import edu.cit.capendit.unisell.product.dto.ProductResponse;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository,
                           CategoryRepository categoryRepository,
                           UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<ProductResponse> getMyProducts(String vendorEmail) {
        return productRepository.findByVendorEmail(vendorEmail)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse createProduct(ProductRequest request, String vendorEmail) {
        validate(request);

        if (productRepository.existsBySkuIgnoreCase(request.getSku().trim())) {
            throw new IllegalArgumentException("SKU already exists");
        }

        Category category = categoryRepository.findByIdAndVendorEmail(request.getCategoryId(), vendorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Category not found or does not belong to you"));

        User vendor = userRepository.findByEmail(vendorEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + vendorEmail));

        Product product = new Product();
        product.setName(request.getName().trim());
        product.setSku(request.getSku().trim());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setLowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 5);
        product.setCategory(category);
        product.setVendor(vendor);

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request, String vendorEmail) {
        validate(request);

        Product product = productRepository.findByIdAndVendorEmail(id, vendorEmail)
                .orElseThrow(() -> new ProductNotFoundException(id));

        String trimmedSku = request.getSku().trim();
        if (!trimmedSku.equalsIgnoreCase(product.getSku())
            && productRepository.existsBySkuIgnoreCase(trimmedSku)) {
            throw new IllegalArgumentException("SKU already exists");
        }

        Category category = categoryRepository.findByIdAndVendorEmail(request.getCategoryId(), vendorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Category not found or does not belong to you"));

        product.setName(request.getName().trim());
        product.setSku(trimmedSku);
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setLowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 5);
        product.setCategory(category);

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public void deleteProduct(Long id, String vendorEmail) {
        Product product = productRepository.findByIdAndVendorEmail(id, vendorEmail)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productRepository.delete(product);
    }

    private void validate(ProductRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (request.getSku() == null || request.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be empty");
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("Category not found or does not belong to you");
        }
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getPrice(),
                product.getQuantity(),
                product.getLowStockThreshold(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }

    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(Long id) {
            super("Product not found with id: " + id);
        }
    }
}