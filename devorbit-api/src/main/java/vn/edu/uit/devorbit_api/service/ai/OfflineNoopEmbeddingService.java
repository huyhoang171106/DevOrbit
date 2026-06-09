package vn.edu.uit.devorbit_api.service.ai;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Deterministic hash-based embedding for tests and offline dev.
 * Produces consistent pseudo-embeddings from text content.
 * NOT suitable for production — use OpenAiCompatibleEmbeddingService instead.
 */
@Service
@ConditionalOnProperty(name = "app.embedding.offline", havingValue = "true")
public class OfflineNoopEmbeddingService implements EmbeddingService {

    private static final int DIMENSIONS = 1536;

    @Override
    public float[] embed(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

            float[] embedding = new float[DIMENSIONS];
            // Expand 32-byte hash into 1536 dimensions via cyclic repetition + normalization
            double norm = 0.0;
            for (int i = 0; i < DIMENSIONS; i++) {
                byte b = hash[i % hash.length];
                embedding[i] = (b & 0xFF) / 255.0f - 0.5f; // range [-0.5, 0.5]
                norm += embedding[i] * embedding[i];
            }
            // L2 normalize
            norm = Math.sqrt(norm);
            if (norm > 0) {
                for (int i = 0; i < DIMENSIONS; i++) {
                    embedding[i] = (float) (embedding[i] / norm);
                }
            }
            return embedding;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    @Override
    public int dimensions() {
        return DIMENSIONS;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
