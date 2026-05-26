package vn.edu.uit.devorbit_api.dto.publicapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * REPO SUMMARY = a compact representation of a GitHub repo.
 *
 * Used across many endpoints:
 *   GET /api/repos/{repoId}
 *   GET /api/courses/{courseId}/repos
 *   GET /api/admin/repos
 *   nested inside CourseDetailResponse.repos
 *
 * The courseId/courseCode/courseName fields are populated when
 * the repo is fetched as part of a course detail (they tell you
 * which course the repo is linked to).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RepoSummaryResponse(
    Long id,
    String displayName,
    String description,
    String githubUrl,
    String primaryLanguage,     // e.g., "Java", "Python", "TypeScript"
    Integer stars,              // GitHub star count
    List<TechStackResponse> techStacks,  // e.g., ["React", "Spring Boot"]
    Long courseId,              // The course this repo is linked to
    String courseCode,          // Course code, e.g., "SE101"
    String courseName           // Course name, e.g., "Nhập môn CNPM"
) {}
