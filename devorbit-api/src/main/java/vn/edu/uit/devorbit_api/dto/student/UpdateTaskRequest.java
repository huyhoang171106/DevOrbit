package vn.edu.uit.devorbit_api.dto.student;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdateTaskRequest(
    String title,
    String description,
    LocalDateTime deadline,
    Boolean completed,
    String recurrence,
    Integer recurrenceDaysOfWeek,
    LocalDate recurrenceStartDate,
    LocalDate recurrenceEndDate
) {}
