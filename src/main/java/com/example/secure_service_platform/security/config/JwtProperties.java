package com.example.secure_service_platform.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;

    private long expiration;
}
