package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotNull;
import vn.edu.uit.devorbit_api.entity.CourseRelationType;

public record CourseRelationshipRequest(
    @NotNull Long courseId,
    @NotNull Long relatedCourseId,
    @NotNull CourseRelationType relationType
) {}
