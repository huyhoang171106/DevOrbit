package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
}
