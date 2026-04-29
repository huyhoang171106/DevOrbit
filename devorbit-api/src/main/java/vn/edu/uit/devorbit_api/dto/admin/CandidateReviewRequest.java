package vn.edu.uit.devorbit_api.dto.admin;

import java.util.List;

public record CandidateReviewRequest(
    String description,
    List<String> techStacks,
    String reviewNote
) {}
