package vn.edu.uit.devorbit_api.dto.admin;

public record ReviewerStatsResponse(
    String reviewer,
    long remaining,
    long completed
) {}
