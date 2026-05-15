package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record ArticleRequest(
    @NotBlank String title,
    @NotBlank String url,
    String author,
    String description
) {}
