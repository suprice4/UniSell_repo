package edu.cit.capendit.unisell.category;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request, Authentication authentication) {
        try {
            CategoryResponse response = categoryService.createCategory(request, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id,
                                             @RequestBody CategoryRequest request,
                                             Authentication authentication) {
        try {
            CategoryResponse response = categoryService.updateCategory(id, request, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (CategoryService.CategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id, Authentication authentication) {
        try {
            categoryService.deleteCategory(id, authentication.getName());
            return ResponseEntity.noContent().build();
        } catch (CategoryService.CategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}