package vn.edu.uit.devorbit_api.dto.publicapi;

import java.util.List;

public record RoadmapRecommendationResponse(
    String summary,
    List<CourseRecommendation> recommendedCourses
) {
    public record CourseRecommendation(
        Long courseId,
        String courseCode,
        String courseName,
        String reasoning,
        String description
    ) {}
}
