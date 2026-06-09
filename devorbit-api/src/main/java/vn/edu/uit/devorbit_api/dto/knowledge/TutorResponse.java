package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.List;

public record TutorResponse(
    String answer,
    List<Citation> citations,
    String confidence
) {}
