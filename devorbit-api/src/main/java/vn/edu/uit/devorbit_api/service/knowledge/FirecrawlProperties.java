package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "firecrawl")
public class FirecrawlProperties {

    private boolean enabled = false;
    private String apiUrl = "https://api.firecrawl.dev";
    private String apiKey = "";
    private int timeoutSeconds = 60;
    private int maxPages = 50;
}
