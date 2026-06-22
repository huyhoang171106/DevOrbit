package vn.edu.uit.devorbit_api.dto.student;

import jakarta.validation.constraints.NotBlank;

public record RespondInviteRequest(
    @NotBlank String action
) {}
