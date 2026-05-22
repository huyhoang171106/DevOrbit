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
        String baseUrl = githubProperties.apiUrl();
        String userAgent = githubProperties.userAgent();

        WebClient.Builder builder = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.USER_AGENT, userAgent);

        if (token == null || token.isBlank()) {
            System.err.println("WARNING: GITHUB_TOKEN is not set. GitHub API scan will be unavailable.");
        } else {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }

        return builder.build();
    }
}
