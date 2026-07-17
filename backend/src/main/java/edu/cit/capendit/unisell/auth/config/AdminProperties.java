package edu.cit.capendit.unisell.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "admin")
public record AdminProperties(boolean enabled, String email, String password) {
}