package vn.edu.uit.devorbit_api.dto.admin;

import java.util.List;

public record ApprovedRepoUpdateRequest(
    String displayName,
    String description,
    List<String> techStacks,
    Boolean active
) {}
