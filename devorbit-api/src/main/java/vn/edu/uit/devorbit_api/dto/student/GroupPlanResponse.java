package vn.edu.uit.devorbit_api.dto.student;

import vn.edu.uit.devorbit_api.entity.GroupPlan;

import java.time.LocalDate;

public record GroupPlanResponse(
    Long id,
    String title,
    String description,
    String creatorStudentCode,
    LocalDate deadline,
    boolean active,
    String createdAt,
    boolean deleteRequested,
    String deleteRequestedBy
) {
    public static GroupPlanResponse from(GroupPlan gp) {
        return new GroupPlanResponse(
            gp.getId(),
            gp.getTitle(),
            gp.getDescription(),
            gp.getCreatorStudentCode(),
            gp.getDeadline(),
            gp.isActive(),
            gp.getCreatedAt() != null ? gp.getCreatedAt().toString() : null,
            gp.isDeleteRequested(),
            gp.getDeleteRequestedBy()
        );
    }
}
