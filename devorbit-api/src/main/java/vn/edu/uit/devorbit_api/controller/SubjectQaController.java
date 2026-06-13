package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
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

    /**
     * Ask the AI Course Assistant a question and get a conversational response.
     */
    @PostMapping("/query")
    public SubjectQaResponse query(@RequestBody @Valid SubjectQaRequest request) {
        return subjectQaService.processQuery(request);
    }

    /**
     * Stream the AI assistant response using Server-Sent Events.
     * Emits status, search_result, delta, complete, and error events.
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestBody @Valid SubjectQaRequest request) {
        return subjectQaService.streamQuery(request);
    }
}
