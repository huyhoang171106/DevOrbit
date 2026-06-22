package vn.edu.uit.devorbit_api.dto.student;

public record UpdateGroupTaskRequest(
    String title,
    String description,
    String assignedTo,
    String deadline,
    Boolean completed
) {}
