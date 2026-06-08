package vn.edu.uit.devorbit_api.dto.student;

public record StudentAuthResponse(String token, Long id, String studentCode, String fullName, String email) {}