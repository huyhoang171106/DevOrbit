package vn.edu.uit.devorbit_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class GithubClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(GithubClientConfig.class);

    @Bean
    public WebClient githubWebClient(GithubProperties githubProperties) {
        String token = githubProperties.token();
        String baseUrl = githubProperties.apiUrl();
        String userAgent = githubProperties.userAgent();

        WebClient.Builder builder = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024));

        if (token == null || token.isBlank()) {
            logger.warn("WARNING: GITHUB_TOKEN is not set. GitHub API scan will be unavailable.");
        } else {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }

        return builder.build();
    }
}
