package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminCourseUpsertRequest(
    @NotBlank String code,
    @NotBlank String name,
    String nameEn,
    @NotNull Integer credits,
    Integer lectureHours,
    Integer practiceHours,
    @NotBlank String subjectType,
    Boolean isOpen,
    String managementUnit,
    String codeOld,
    String equivalentMH,
    String prerequisiteMH,
    String previousMH,
    String description
) {}
