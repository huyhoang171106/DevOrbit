package vn.edu.uit.devorbit_api.dto.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

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
        String reasoning,
        String description,
        boolean isMandatory,
        Integer semester,
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
        int targetTC,
        int currentTC,
        List<ElectiveCandidate> candidates
    ) {}

    public record ElectiveCandidate(
        Long courseId,
        String courseCode,
        String courseName,
        int credits,
        int score,
        boolean isSelected,
        String description,
        String reasoning,
        Integer semester
    ) {}
}
