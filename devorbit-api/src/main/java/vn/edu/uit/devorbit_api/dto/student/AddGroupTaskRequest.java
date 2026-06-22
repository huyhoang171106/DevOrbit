package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotBlank;

public record AddGroupTaskRequest(
    @NotBlank String title,
    String description,
    String assignedTo,
    String deadline
) {}
