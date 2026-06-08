package vn.edu.uit.devorbit_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.github")
public record GithubProperties(String apiUrl, String userAgent, String token) {
    public GithubProperties {
        if (apiUrl == null || apiUrl.isBlank()) apiUrl = "https://api.github.com";
        if (userAgent == null || userAgent.isBlank()) userAgent = "DevOrbit/1.0";
    }
}
