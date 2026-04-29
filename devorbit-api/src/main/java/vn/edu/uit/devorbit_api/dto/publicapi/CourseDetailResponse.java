package vn.edu.uit.devorbit_api.dto.publicapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CourseDetailResponse(
    Long id,
    String code,
    String name,
    String description,
    Integer theoryHours,
    Integer practiceHours,
    Integer credits,
    List<RepoSummaryResponse> repos
) {}
