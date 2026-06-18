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
 * @JsonInclude(NON_NULL): optional fields like readmeExcerpt, fileTree,
 * courseName only appear in JSON if they have a value. This keeps the
 * response payload small when those fields haven't been fetched yet.
 *
 * techStacks includes full TechStackResponse objects (not just names)
 * so the frontend gets display-ready data without a second call.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    String approvedAt,
    String repoType,
    String usefulnessRating,
    Integer usefulnessScore,
    String readyToUseLevel,
    Integer reviewCount,
    Double averageRating
) {}
