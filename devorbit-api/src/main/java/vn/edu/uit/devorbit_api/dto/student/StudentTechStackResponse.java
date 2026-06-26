package vn.edu.uit.devorbit_api.dto.student;

public record StudentTechStackResponse(
    Long id,
    Long techStackId,
    String techStackName,
    String createdAt
) {}
