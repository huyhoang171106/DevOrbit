package vn.edu.uit.devorbit_api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for knowledge ingestion pipeline.
 */
@Configuration
@ConfigurationProperties(prefix = "devorbit.knowledge")
@Getter
@Setter
public class KnowledgeConfig {

    /**
     * Directory containing marker-generated markdown files.
     */
    private String markerMdDir = "./data/processed/marker-md";

    /**
     * Whether ingestion is enabled.
     */
    private boolean ingestionEnabled = true;

    /**
     * Embedding dimensions (must match the model used).
     */
    private int embeddingDimensions = 1536;
}
