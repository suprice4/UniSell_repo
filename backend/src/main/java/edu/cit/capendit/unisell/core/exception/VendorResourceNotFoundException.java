package edu.cit.capendit.unisell.core.exception;

public abstract class VendorResourceNotFoundException extends RuntimeException {
    protected VendorResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
    }
}