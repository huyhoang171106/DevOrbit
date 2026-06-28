package vn.edu.uit.devorbit_api.dto.auth;

public record TokenPairResponse(
    String accessToken,
    String refreshToken,
    String tokenType
) {}
