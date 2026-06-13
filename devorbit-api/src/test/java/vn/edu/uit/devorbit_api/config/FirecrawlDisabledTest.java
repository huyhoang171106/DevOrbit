package vn.edu.uit.devorbit_api.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * Verifies the application context starts successfully with Firecrawl disabled.
 * If Firecrawl services fail to load conditionally, this test will catch it.
 */
@SpringBootTest(properties = {
    "firecrawl.enabled=false",
    "devorbit.knowledge.schema-init.enabled=false"
})
@ActiveProfiles("test")
class FirecrawlDisabledTest {

    @Test
    void contextLoadsWithFirecrawlDisabled() {
        assertThatNoException();
    }
}
