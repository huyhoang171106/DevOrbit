package vn.edu.uit.devorbit_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.github")
public record GithubProperties(String token) {}
