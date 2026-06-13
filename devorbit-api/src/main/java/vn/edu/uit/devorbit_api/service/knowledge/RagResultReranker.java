package vn.edu.uit.devorbit_api.service.knowledge;

import org.springframework.stereotype.Component;
import vn.edu.uit.devorbit_api.entity.KnowledgeChunk;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RagResultReranker {

    /**
     * Rerank candidates using lexical overlap boost, section-title match, and source diversity.
     *
     * @param query      the original user query
     * @param candidates candidate ChunkResults from hybrid/vector search
     * @param limit      max results to return
     * @return reranked and deduplicated list
     */
    public List<KnowledgeRetrievalService.ChunkResult> rerank(
            String query,
            List<KnowledgeRetrievalService.ChunkResult> candidates,
            int limit) {

        if (candidates == null || candidates.isEmpty() || limit <= 0) {
            return List.of();
        }

        // 1. Deduplicate by chunk ID; if ID is null, dedupe by courseCode + sectionTitle + first 120 chars
        List<KnowledgeRetrievalService.ChunkResult> deduped = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (KnowledgeRetrievalService.ChunkResult cr : candidates) {
            KnowledgeChunk chunk = cr.chunk();
            String key;
            if (chunk.getId() != null) {
                key = chunk.getId().toString();
            } else {
                String textPrefix = chunk.getChunkText() != null
                        ? chunk.getChunkText().substring(0, Math.min(120, chunk.getChunkText().length()))
                        : "";
                key = chunk.getCourseCode() + "|" + chunk.getSectionTitle() + "|" + textPrefix;
            }
            if (seen.add(key)) {
                deduped.add(cr);
            }
        }

        if (deduped.isEmpty()) {
            return List.of();
        }

        // 2. Tokenize query for lexical matching
        String normalizedQuery = normalize(query);
        Set<String> queryTokens = new HashSet<>();
        for (String token : normalizedQuery.split("\\s+")) {
            if (token.length() >= 3) {
                queryTokens.add(token);
            }
        }

        // 3. Score and rerank
        List<ScoredResult> scored = new ArrayList<>();
        for (KnowledgeRetrievalService.ChunkResult cr : deduped) {
            KnowledgeChunk chunk = cr.chunk();
            double lexicalOverlapBoost = 0.0;
            double sectionBoost = 0.0;

            if (!queryTokens.isEmpty()) {
                String chunkText = normalize(chunk.getChunkText() != null ? chunk.getChunkText() : "");
                String sectionTitle = normalize(chunk.getSectionTitle() != null ? chunk.getSectionTitle() : "");

                long overlapCount = 0;
                for (String token : queryTokens) {
                    if (chunkText.contains(token)) {
                        overlapCount++;
                    }
                }

                lexicalOverlapBoost = Math.min(0.030, overlapCount * 0.006);

                // Section title match boost
                for (String token : queryTokens) {
                    if (sectionTitle.contains(token)) {
                        sectionBoost = 0.010;
                        break;
                    }
                }
            }

            double newScore = cr.score() + lexicalOverlapBoost + sectionBoost;
            scored.add(new ScoredResult(cr, newScore));
        }

        // Sort by new score descending
        scored.sort((a, b) -> Double.compare(b.newScore, a.newScore));

        // 4. Apply source diversity: two-pass approach
        List<KnowledgeRetrievalService.ChunkResult> diversified = new ArrayList<>();
        Set<ScoredResult> selected = new HashSet<>();
        java.util.Map<Object, Integer> sourceCount = new java.util.HashMap<>();

        // Pass 1: Select up to 2 highest-scoring chunks per source
        for (ScoredResult sr : scored) {
            KnowledgeChunk chunk = sr.result.chunk();
            Object sourceKey = chunk.getSource() != null && chunk.getSource().getId() != null
                    ? chunk.getSource().getId()
                    : (chunk.getCourseCode() != null ? chunk.getCourseCode() : "default");

            int count = sourceCount.getOrDefault(sourceKey, 0);
            if (count < 2) {
                diversified.add(new KnowledgeRetrievalService.ChunkResult(
                        sr.result.chunk(), sr.newScore));
                sourceCount.put(sourceKey, count + 1);
                selected.add(sr);
            }

            if (diversified.size() >= limit) {
                break;
            }
        }

        // Pass 2: Fill the remaining slots up to the limit using the highest scoring remaining items across all sources
        if (diversified.size() < limit) {
            for (ScoredResult sr : scored) {
                if (!selected.contains(sr)) {
                    diversified.add(new KnowledgeRetrievalService.ChunkResult(
                            sr.result.chunk(), sr.newScore));
                    selected.add(sr);

                    if (diversified.size() >= limit) {
                        break;
                    }
                }
            }
        }

        return diversified;
    }

    private String normalize(String text) {
        if (text == null) return "";
        String nfd = Normalizer.normalize(text.toLowerCase(), Normalizer.Form.NFD);
        return nfd.replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "d")
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private record ScoredResult(KnowledgeRetrievalService.ChunkResult result, double newScore) {}
}
