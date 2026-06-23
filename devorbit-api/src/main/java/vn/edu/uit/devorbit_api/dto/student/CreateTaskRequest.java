package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateTaskRequest(
    @NotBlank String title,
    String description,
    LocalDateTime deadline,
    String recurrence,
    Integer recurrenceDaysOfWeek,
    LocalDate recurrenceStartDate,
    LocalDate recurrenceEndDate
) {}
