package vn.edu.uit.devorbit_api.dto.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Structured repository info included in AI tutor responses.
 * Carries vote scores, ratings, and metadata for rendering repo cards.
 */
public record DevOrbitRepoInfo(
    Long id,
    String name,
    String description,
    @JsonProperty("githubUrl") String githubUrl,
    @JsonProperty("primaryLanguage") String primaryLanguage,
    Integer stars,
    @JsonProperty("techStacks") List<String> techStacks,
    @JsonProperty("courseCode") String courseCode,
    @JsonProperty("courseName") String courseName,
    @JsonProperty("voteScore") Integer voteScore,
    @JsonProperty("averageRating") Double averageRating,
    @JsonProperty("reviewCount") Integer reviewCount,
    @JsonProperty("viewCount") Integer viewCount,
    @JsonProperty("usefulnessRating") String usefulnessRating,
    @JsonProperty("usefulnessScore") Integer usefulnessScore
) {}
