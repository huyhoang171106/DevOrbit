package vn.edu.uit.devorbit_api.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for AI services.
 * Provides WebClient bean with timeout settings for LLM API calls.
 */
@Configuration
public class AiConfig {

    @Value("${app.opencode.api-url}")
    private String apiUrl;

    @Value("${app.opencode.api-key:}")
    private String apiKey;

    @Value("${app.opencode.model:deepseek-v4-flash}")
    private String model;

    @Value("${app.opencode.timeout-seconds:90}")
    private int timeoutSeconds = 90;

    /**
     * WebClient bean with timeout configuration for LLM API calls.
     * Connect timeout: 10 seconds
     * Read timeout: configurable, 30 seconds by default
     */
    @Bean
    public WebClient aiWebClient() {
        ConnectionProvider pool = ConnectionProvider.builder("ai-webclient-pool")
            .maxConnections(50)
            .maxIdleTime(Duration.ofSeconds(30))
            .maxLifeTime(Duration.ofMinutes(5))
            .pendingAcquireTimeout(Duration.ofSeconds(30))
            .evictInBackground(Duration.ofSeconds(60))
            .build();

        int timeout = getTimeoutSeconds();
        HttpClient httpClient = HttpClient.create(pool)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .responseTimeout(Duration.ofSeconds(timeout))
            .doOnConnected(conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.SECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.SECONDS))
            );

        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .defaultHeader("Content-Type", "application/json")
            .build();
    }

    /**
     * Check if LLM API is configured and enabled.
     * @return true if API key is non-empty
     */
    public boolean isLlmEnabled() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getModel() {
        return model;
    }

    public int getTimeoutSeconds() {
        return Math.max(5, timeoutSeconds);
    }
}
