package vn.edu.uit.devorbit_api.dto.publicapi;

public record CourseSummaryResponse(Long id, String code, String name, String description, Long repoCount, Integer semester, String loaiMonHoc, String managementUnit) {}
