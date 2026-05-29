package vn.edu.uit.devorbit_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

class GithubScanServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void usesCommitterDateFromLatestCommit() throws Exception {
        GithubScanService service = serviceWithCommits("""
            [{"commit":{"committer":{"date":"2026-04-20T10:00:00Z"},"author":{"date":"2026-04-19T10:00:00Z"}}}]
            """);

        String date = service.resolveLatestActivityAt("owner", "repo", metadata("""
            {"default_branch":"main","pushed_at":"2026-03-01T00:00:00Z","updated_at":"2026-02-01T00:00:00Z"}
            """));

        assertThat(date).isEqualTo("2026-04-20T10:00:00Z");
    }

    @Test
    void fallsBackToAuthorDateWhenCommitterDateIsMissing() throws Exception {
        GithubScanService service = serviceWithCommits("""
            [{"commit":{"committer":{},"author":{"date":"2026-04-19T10:00:00Z"}}}]
            """);

        String date = service.resolveLatestActivityAt("owner", "repo", metadata("""
            {"default_branch":"main","pushed_at":"2026-03-01T00:00:00Z"}
            """));

        assertThat(date).isEqualTo("2026-04-19T10:00:00Z");
    }

    @Test
    void fallsBackToPushedAtWhenCommitsAreEmpty() throws Exception {
        GithubScanService service = serviceWithCommits("[]");

        String date = service.resolveLatestActivityAt("owner", "repo", metadata("""
            {"default_branch":"main","pushed_at":"2026-03-01T00:00:00Z","updated_at":"2026-02-01T00:00:00Z"}
            """));

        assertThat(date).isEqualTo("2026-03-01T00:00:00Z");
    }

    @Test
    void fallsBackToPushedAtWhenCommitsApiFails() throws Exception {
        GithubScanService service = serviceWithExchange(request -> {
            if (request.url().getPath().endsWith("/commits")) {
                return Mono.error(new RuntimeException("rate limited"));
            }
            return jsonResponse("{}");
        });

        String date = service.resolveLatestActivityAt("owner", "repo", metadata("""
            {"default_branch":"main","pushed_at":"2026-03-01T00:00:00Z","updated_at":"2026-02-01T00:00:00Z"}
            """));

        assertThat(date).isEqualTo("2026-03-01T00:00:00Z");
    }

    @Test
    void fallsBackToUpdatedAtWhenPushedAtIsMissing() throws Exception {
        GithubScanService service = serviceWithCommits("[]");

        String date = service.resolveLatestActivityAt("owner", "repo", metadata("""
            {"default_branch":"main","updated_at":"2026-02-01T00:00:00Z"}
            """));

        assertThat(date).isEqualTo("2026-02-01T00:00:00Z");
    }

    @Test
    void doesNotUseCreatedAtWhenActivityDatesAreMissing() throws Exception {
        GithubScanService service = serviceWithCommits("[]");

        String date = service.resolveLatestActivityAt("owner", "repo", metadata("""
            {"created_at":"2020-01-01T00:00:00Z"}
            """));

        assertThat(date).isNull();
    }

    private GithubScanService serviceWithCommits(String commitsJson) {
        return serviceWithExchange(request -> jsonResponse(commitsJson));
    }

    private GithubScanService serviceWithExchange(ExchangeFunction exchangeFunction) {
        WebClient webClient = WebClient.builder()
            .baseUrl("https://api.github.com")
            .exchangeFunction(exchangeFunction)
            .build();
        return new GithubScanService(null, null, null, webClient);
    }

    private JsonNode metadata(String json) throws Exception {
        return objectMapper.readTree(json);
    }

    private Mono<ClientResponse> jsonResponse(String body) {
        return Mono.just(ClientResponse.create(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(body)
            .build());
    }
}
