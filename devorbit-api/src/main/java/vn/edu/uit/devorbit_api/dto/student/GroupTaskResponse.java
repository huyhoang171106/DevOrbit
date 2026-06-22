package vn.edu.uit.devorbit_api.dto.student;

import vn.edu.uit.devorbit_api.entity.GroupTask;

import java.time.LocalDate;

public record GroupTaskResponse(
    Long id,
    Long groupPlanId,
    String title,
    String description,
    String assignedTo,
    LocalDate deadline,
    boolean completed,
    String createdBy,
    boolean deleteRequested,
    String deleteRequestedBy,
    String createdAt,
    String updatedAt
) {
    public static GroupTaskResponse from(GroupTask t) {
        return new GroupTaskResponse(
            t.getId(),
            t.getGroupPlan().getId(),
            t.getTitle(),
            t.getDescription(),
            t.getAssignedTo(),
            t.getDeadline(),
            t.isCompleted(),
            t.getCreatedBy(),
            t.isDeleteRequested(),
            t.getDeleteRequestedBy(),
            t.getCreatedAt() != null ? t.getCreatedAt().toString() : null,
            t.getUpdatedAt() != null ? t.getUpdatedAt().toString() : null
        );
    }
}
