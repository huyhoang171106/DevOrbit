package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminCourseUpsertRequest(
    @NotBlank String code,
    @NotBlank String name,
    @NotNull Integer credits,
    Integer lectureHours,
    Integer practiceHours,
    @NotBlank String subjectType,
    String description
) {}
