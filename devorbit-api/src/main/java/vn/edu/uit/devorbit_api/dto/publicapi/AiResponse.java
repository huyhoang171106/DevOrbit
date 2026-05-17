package vn.edu.uit.devorbit_api.dto.publicapi;

public record AiResponse(
    String content,
    String type // SUMMARY, TUTOR_ADVICE, ROADMAP
) {}
