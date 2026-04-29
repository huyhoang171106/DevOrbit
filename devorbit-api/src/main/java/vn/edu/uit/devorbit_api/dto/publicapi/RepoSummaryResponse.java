package vn.edu.uit.devorbit_api.dto.publicapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RepoSummaryResponse(
    Long id,
    String displayName,
    String description,
    String githubUrl,
    String primaryLanguage,
    Integer stars,
    List<TechStackResponse> techStacks
) {}
