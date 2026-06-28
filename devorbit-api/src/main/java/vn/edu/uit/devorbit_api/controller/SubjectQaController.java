package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import vn.edu.uit.devorbit_api.dto.publicapi.SubjectQaRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.SubjectQaResponse;
import vn.edu.uit.devorbit_api.service.SubjectQaService;

/**
 * Controller exposing AI Q&A endpoints for course advisory.
 */
@RestController
@RequestMapping("/api/ai/subject-qa")
@RequiredArgsConstructor
public class SubjectQaController {

    private final SubjectQaService subjectQaService;
    private final ObjectMapper objectMapper;

    @PostMapping("/query")
    public org.springframework.http.ResponseEntity<SubjectQaResponse> query(@RequestBody @Valid SubjectQaRequest request) {
        long start = System.currentTimeMillis();
        SubjectQaResponse response = subjectQaService.processQuery(request);
        long elapsed = System.currentTimeMillis() - start;

        return org.springframework.http.ResponseEntity.ok()
            .header("X-Response-Time", elapsed + "ms")
            .header("Cache-Control", "no-cache, no-store, must-revalidate")
            .body(response);
    }

    /**
     * Stream the AI assistant response using Server-Sent Events.
     * Emits status, search_result, delta, complete, and error events.
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> stream(
            @RequestBody @Valid SubjectQaRequest request,
            HttpServletResponse response) {
        response.setBufferSize(1);
        StreamingResponseBody body = outputStream -> {
            java.io.BufferedWriter writer = new java.io.BufferedWriter(
                new java.io.OutputStreamWriter(outputStream, java.nio.charset.StandardCharsets.UTF_8)
            );
            subjectQaService.streamQuery(request, event -> {
                synchronized (writer) {
                    try {
                        writer.write("event:");
                        writer.write(event.type());
                        writer.write("\n");
                        writer.write("data:");
                        writer.write(objectMapper.writeValueAsString(event));
                        writer.write("\n\n");
                        writer.flush();
                        outputStream.flush();
                        response.flushBuffer();
                    } catch (java.io.IOException e) {
                        throw new java.io.UncheckedIOException(e);
                    }
                }
            });
        };

        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_EVENT_STREAM)
            .header("Cache-Control", "no-cache, no-store, must-revalidate")
            .header("X-Accel-Buffering", "no")
            .header("Content-Encoding", "identity")
            .body(body);
    }
}
