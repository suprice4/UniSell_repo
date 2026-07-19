package edu.cit.capendit.unisell.category.service;

import edu.cit.capendit.unisell.auth.model.User;
import edu.cit.capendit.unisell.auth.repository.UserRepository;
import edu.cit.capendit.unisell.category.dto.CategoryRequest;
import edu.cit.capendit.unisell.category.dto.CategoryResponse;
import edu.cit.capendit.unisell.category.model.Category;
import edu.cit.capendit.unisell.category.repository.CategoryRepository;
import edu.cit.capendit.unisell.core.exception.VendorResourceNotFoundException;
import edu.cit.capendit.unisell.core.validation.NameValidator;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<CategoryResponse> getMyCategories(String vendorEmail) {
        return categoryRepository.findByVendorEmail(vendorEmail)
                .stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
    }

    public CategoryResponse createCategory(CategoryRequest request, String vendorEmail) {
        NameValidator.validateName(request.getName(), "Category");

        if (categoryRepository.existsByNameIgnoreCaseAndVendorEmail(request.getName().trim(), vendorEmail)) {
            throw new IllegalArgumentException("You already have a category named '" + request.getName().trim() + "'");
        }

        User vendor = userRepository.findByEmail(vendorEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + vendorEmail));

        Category category = new Category();
        category.setName(request.getName().trim());
        category.setVendor(vendor);

        Category saved = categoryRepository.save(category);
        return new CategoryResponse(saved.getId(), saved.getName());
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request, String vendorEmail) {
        NameValidator.validateName(request.getName(), "Category");

        Category category = categoryRepository.findByIdAndVendorEmail(id, vendorEmail)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        String trimmedName = request.getName().trim();

        if (!trimmedName.equalsIgnoreCase(category.getName())
            && categoryRepository.existsByNameIgnoreCaseAndVendorEmail(trimmedName, vendorEmail)) {
            throw new IllegalArgumentException("You already have a category named '" + trimmedName + "'");
        }

        category.setName(trimmedName);
        Category saved = categoryRepository.save(category);
        return new CategoryResponse(saved.getId(), saved.getName());
    }

    public void deleteCategory(Long id, String vendorEmail) {
        Category category = categoryRepository.findByIdAndVendorEmail(id, vendorEmail)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryRepository.delete(category);
    }

    public static class CategoryNotFoundException extends VendorResourceNotFoundException {
        public CategoryNotFoundException(Long id) {
            super("Category", id);
        }
    }
}