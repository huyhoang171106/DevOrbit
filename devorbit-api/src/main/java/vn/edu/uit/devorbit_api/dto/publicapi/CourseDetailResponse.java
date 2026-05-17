package vn.edu.uit.devorbit_api.dto.publicapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CourseDetailResponse(
    Long id,
    String code,
    String name,
    String nameEn,
    String description,
    Integer theoryHours,
    Integer practiceHours,
    Integer credits,
    String subjectType,
    Boolean isOpen,
    String managementUnit,
    String codeOld,
    String equivalentMH,
    String prerequisiteMH,
    String previousMH,
    List<RepoSummaryResponse> repos
) {}
