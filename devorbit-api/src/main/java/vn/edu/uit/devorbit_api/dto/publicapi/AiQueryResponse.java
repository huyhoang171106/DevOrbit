package vn.edu.uit.devorbit_api.dto.publicapi;

import java.util.List;

public record AiQueryResponse(
    String answer,
    List<Long> relevantNodeIds,
    String type // "PREREQUISITE", "DOWNSTREAM", "IMPACT", "SEMESTER", "STATS", "UNKNOWN"
) {}
