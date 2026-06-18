package vn.edu.uit.devorbit_api.dto.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * ROADMAP RECOMMENDATION RESPONSE = AI-generated 4-year study plan.
 *
 * Main output of the graduation roadmap feature:
 *   summary            → AI-written overview
 *   recommendedCourses → ordered list semester-by-semester
 *   graduationTracks   → specialization tracks with AI recommendation
 *   electivePools      → elective groups with AI-ranked picks
 *
 * Each elective candidate has a "score" (relevance) and
 * "isSelected" flag (AI-picked best choices).
 */
public record RoadmapRecommendationResponse(
    String summary,
    List<CourseRecommendation> recommendedCourses,
    List<GraduationTrack> graduationTracks,
    List<ElectivePoolCandidates> electivePools
) {
    public record CourseRecommendation(
        Long courseId,
        String courseCode,
        String courseName,
        String reasoning,       // Why the AI recommends this
        String description,
        boolean isMandatory,    // Required for graduation?
        Integer semester,       // When to take it
        int credits
    ) {}

    public record GraduationTrack(
        @JsonProperty("type") String type,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("credits") int credits,
        @JsonProperty("requirements") String requirements,
        @JsonProperty("recommendation") String recommendation,
        @JsonProperty("recommended") boolean recommended,
        @JsonProperty("courseCodes") List<String> courseCodes
    ) {}

    public record ElectivePoolCandidates(
        String poolId,
        String poolName,
        int targetTC,               // Target credits needed
        int currentTC,              // Current earned credits
        List<ElectiveCandidate> candidates
    ) {}

    public record ElectiveCandidate(
        Long courseId,
        String courseCode,
        String courseName,
        int credits,
        int score,              // AI relevance (higher = better)
        boolean isSelected,     // AI recommends picking this
        String description,
        String reasoning,
        Integer semester
    ) {}
}
