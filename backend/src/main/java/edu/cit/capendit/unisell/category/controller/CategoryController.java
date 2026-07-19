package edu.cit.capendit.unisell.category.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import edu.cit.capendit.unisell.category.dto.CategoryRequest;
import edu.cit.capendit.unisell.category.dto.CategoryResponse;
import edu.cit.capendit.unisell.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getMyCategories(Authentication authentication) {
        return ResponseEntity.ok(categoryService.getMyCategories(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request,
                                                             Authentication authentication) {
        CategoryResponse response = categoryService.createCategory(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
                                                             @RequestBody CategoryRequest request,
                                                             Authentication authentication) {
        CategoryResponse response = categoryService.updateCategory(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id, Authentication authentication) {
        categoryService.deleteCategory(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}