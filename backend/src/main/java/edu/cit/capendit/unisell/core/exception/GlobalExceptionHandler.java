package edu.cit.capendit.unisell.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Scoped deliberately to Category/Platform/Product controllers only.
// Order and Inventory handle their own exceptions locally (different shape,
// out of scope for this cleanup) and are unaffected by this advice.
@RestControllerAdvice(basePackages = {
        "edu.cit.capendit.unisell.category.controller",
        "edu.cit.capendit.unisell.platform.controller",
        "edu.cit.capendit.unisell.product.controller"
})
public class GlobalExceptionHandler {

    @ExceptionHandler(VendorResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(VendorResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}