package vn.edu.uit.devorbit_api.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vn.edu.uit.devorbit_api.config.AiConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OpenCodeAiServiceTest {

    @Test
    void streamCompletion_emitsParsedDeltasFromProviderStream() {
        WebClient webClient = WebClient.builder()
            .exchangeFunction(request -> Mono.just(ClientResponse.create(HttpStatus.OK)
                .header("Content-Type", MediaType.TEXT_EVENT_STREAM_VALUE)
                .body("""
                    data: {"choices":[{"delta":{"content":null}}]}

                    data: {"choices":[{"delta":{}}]}

                    data: {"choices":[{"delta":{"content":"Xin "}}]}

                    data: {"choices":[{"delta":{"content":"chao"}}]}

                    data: [DONE]

                    """)
                .build()))
            .build();

        AiConfig aiConfig = new AiConfig();
        ReflectionTestUtils.setField(aiConfig, "apiUrl", "https://example.test/v1");
        ReflectionTestUtils.setField(aiConfig, "apiKey", "test-key");
        ReflectionTestUtils.setField(aiConfig, "model", "test-model");

        OpenCodeAiService service = new OpenCodeAiService(webClient, aiConfig, new ObjectMapper());

        List<String> deltas = service.streamCompletion("system", "user")
            .collectList()
            .block();

        assertThat(deltas).containsExactly("Xin ", "chao");
    }
}
