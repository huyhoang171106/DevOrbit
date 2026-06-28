package vn.edu.uit.devorbit_api.dto.student;

import vn.edu.uit.devorbit_api.entity.PersonalTask;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
    Long id,
    String title,
    String description,
    LocalDateTime deadline,
    boolean completed,
    String recurrence,
    Integer recurrenceDaysOfWeek,
    LocalDate recurrenceStartDate,
    LocalDate recurrenceEndDate,
    String createdAt,
    String updatedAt
) {
    public static TaskResponse from(PersonalTask t) {
        return new TaskResponse(
            t.getId(),
            t.getTitle(),
            t.getDescription(),
            t.getDeadline(),
            t.isCompleted(),
            t.getRecurrence(),
            t.getRecurrenceDaysOfWeek(),
            t.getRecurrenceStartDate(),
            t.getRecurrenceEndDate(),
            t.getCreatedAt() != null ? t.getCreatedAt().toString() : null,
            t.getUpdatedAt() != null ? t.getUpdatedAt().toString() : null
        );
    }
}
