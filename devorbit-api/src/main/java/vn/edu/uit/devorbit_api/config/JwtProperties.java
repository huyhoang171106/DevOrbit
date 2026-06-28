package vn.edu.uit.devorbit_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, long expirationMinutes, long refreshExpirationDays) {
    public JwtProperties {
        if (refreshExpirationDays <= 0) refreshExpirationDays = 7;
    }
}
