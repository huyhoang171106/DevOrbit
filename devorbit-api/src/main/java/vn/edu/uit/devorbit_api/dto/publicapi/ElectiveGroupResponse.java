package vn.edu.uit.devorbit_api.dto.publicapi;

import java.util.List;

public record ElectiveGroupResponse(
    String code,
    String name,
    String description,
    int minCredits,
    String parentGroupCode,
    int courseCount
) {}
