package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotNull;

public record GithubScanRequest(@NotNull Long courseId, @NotNull String query) {}
