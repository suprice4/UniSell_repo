package edu.cit.capendit.unisell.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test/protected")
    public String protectedRoute(Authentication authentication) {
        return "Authenticated as: " + authentication.getName()
                + " | authorities: " + authentication.getAuthorities();
    }

    @GetMapping("/api/test/admin-only")
    public String adminOnlyRoute(Authentication authentication) {
        return "Admin access granted for: " + authentication.getName();
    }
}