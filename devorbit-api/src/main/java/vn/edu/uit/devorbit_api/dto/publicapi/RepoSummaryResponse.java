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
    String displayName,              // Friendly name (may differ from GitHub repo name)
    String description,              // GitHub repo description
    String githubUrl,                // Full URL: https://github.com/user/repo
    String primaryLanguage,          // "Java", "Python", "TypeScript", etc.
    Integer stars,                   // GitHub star count
    List<TechStackResponse> techStacks,  // Technology tags with full objects
    Long courseId,                   // The course this repo belongs to
    String courseCode,               // Course code e.g., "SE101" (denormalized for convenience)
    String courseName,               // Course name (denormalized for convenience)
    String readmeExcerpt,            // Short README snippet for quick preview
    String fileTree,                 // Newline-separated file tree structure
    Boolean hasReadme,               // Whether GitHub reported a README
    String lastPushedAt,             // ISO date of last push/commit
    String repoType,
    String usefulnessRating,
    Integer usefulnessScore,
    String readyToUseLevel,
    Integer reviewCount,
    Double averageRating
) {}
