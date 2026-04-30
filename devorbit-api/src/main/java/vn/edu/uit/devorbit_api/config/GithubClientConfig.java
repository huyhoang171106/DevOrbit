package vn.edu.uit.devorbit_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GithubClientConfig {

    @Bean
    public WebClient githubWebClient(GithubProperties githubProperties) {
        String token = githubProperties.token();
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("GITHUB_TOKEN environment variable is not set. GitHub API scan is unavailable.");
        }

        return WebClient.builder()
            .baseUrl("https://api.github.com")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .defaultHeader(HttpHeaders.USER_AGENT, "DevOrbit/1.0")
            .build();
    }
}
