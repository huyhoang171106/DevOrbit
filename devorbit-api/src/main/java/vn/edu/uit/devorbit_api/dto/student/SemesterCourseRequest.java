package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotNull;

public record SemesterCourseRequest(
    @NotNull Long courseId
) {}
