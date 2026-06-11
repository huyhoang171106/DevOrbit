package vn.edu.uit.devorbit_api.service.knowledge;

import java.util.List;
import java.util.Set;

public record RagQueryPlan(
    String originalQuery,
    String primaryQuery,
    String textQuery,
    List<String> expandedQueries,
    Set<String> detectedCourseCodes
) {}
