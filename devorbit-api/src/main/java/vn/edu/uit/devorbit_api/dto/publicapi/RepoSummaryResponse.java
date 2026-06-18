package vn.edu.uit.devorbit_api.dto.publicapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * REPO SUMMARY = a compact representation of a GitHub repo.
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
    String repoType,
    String usefulnessRating,
    Integer usefulnessScore,
    String readyToUseLevel,
    Integer reviewCount,
    Double averageRating
) {}
