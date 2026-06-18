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
 */
public record RepoSummaryResponse(
    Long id,
    String displayName,
    String description,
    String githubUrl,
    String primaryLanguage,
    Integer stars,
    List<TechStackResponse> techStacks,
    Long courseId,
    String courseCode,
    String courseName,
    String readmeExcerpt,
    String fileTree,
    Boolean hasReadme,
    String lastPushedAt,
    Integer reviewCount,
    Double averageRating
) {}
