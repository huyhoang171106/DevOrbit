package vn.edu.uit.devorbit_api.service.ai;

import java.util.List;

/**
 * Abstraction for text embedding generation.
 * Implementations may call external APIs or provide offline fallbacks.
 */
public interface EmbeddingService {

    /**
     * Generate embedding for a single text.
     * @param text text to embed
     * @return embedding vector as float array
     */
    float[] embed(String text);

    /**
     * Generate embeddings for multiple texts in batch.
     * Default implementation calls embed() one-by-one; override for batch API support.
     * @param texts texts to embed
     * @return list of embedding vectors, same order as input
     */
    default List<float[]> embedBatch(List<String> texts) {
        return texts.stream().map(this::embed).toList();
    }

    /**
     * Get the dimension of embeddings produced by this service.
     */
    int dimensions();

    /**
     * Check if this service is enabled and ready.
     */
    boolean isEnabled();
}
