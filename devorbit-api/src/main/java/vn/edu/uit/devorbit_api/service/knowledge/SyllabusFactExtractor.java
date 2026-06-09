package vn.edu.uit.devorbit_api.service.knowledge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.uit.devorbit_api.dto.knowledge.ExtractedSyllabusFacts;
import vn.edu.uit.devorbit_api.service.ai.OpenCodeAiService;

/**
 * Extracts structured syllabus facts from markdown using LLM.
 * Uses OpenCodeAiService for LLM calls.
 */
@Slf4j
@Component
public class SyllabusFactExtractor {

    private final OpenCodeAiService openCodeAiService;
    private final ObjectMapper objectMapper;

    public SyllabusFactExtractor(OpenCodeAiService openCodeAiService) {
        this.openCodeAiService = openCodeAiService;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Extraction result.
     */
    public record ExtractionResult(
        ExtractedSyllabusFacts facts,
        String errorMessage
    ) {
        public static ExtractionResult success(ExtractedSyllabusFacts facts) {
            return new ExtractionResult(facts, null);
        }

        public static ExtractionResult failure(String errorMessage) {
            return new ExtractionResult(null, errorMessage);
        }

        public boolean isSuccess() {
            return facts != null && errorMessage == null;
        }
    }

    /**
     * Extract syllabus facts from markdown content.
     */
    public ExtractionResult extract(String markdownContent) {
        String systemPrompt = buildSystemPrompt();
        String userMessage = buildUserMessage(markdownContent);

        try {
            String response = openCodeAiService.generateCompletion(systemPrompt, userMessage);
            if (response == null || response.isBlank()) {
                return ExtractionResult.failure("LLM returned empty response");
            }

            // Strip markdown fences if present
            String jsonStr = stripMarkdownFences(response.trim());

            JsonNode jsonNode = objectMapper.readTree(jsonStr);
            ExtractedSyllabusFacts facts = objectMapper.treeToValue(jsonNode, ExtractedSyllabusFacts.class);

            if (facts.courseCode() == null || facts.courseCode().isBlank()) {
                return ExtractionResult.failure("LLM response missing courseCode");
            }

            log.info("Successfully extracted facts for course: {}", facts.courseCode());
            return ExtractionResult.success(facts);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse LLM JSON response: {}", e.getMessage());
            return ExtractionResult.failure("Invalid JSON from LLM: " + e.getMessage());
        } catch (Exception e) {
            log.error("LLM extraction failed: {}", e.getMessage());
            return ExtractionResult.failure("LLM extraction failed: " + e.getMessage());
        }
    }

    private String buildSystemPrompt() {
        return """
You are a precise syllabus data extractor. Given a course syllabus in markdown format, extract structured facts as JSON.

RULES:
- Return STRICT JSON only. No markdown fences, no commentary, no explanations.
- Preserve Vietnamese text exactly as-is. Do not translate.
- Use null for missing values. Do not invent or guess data.
- Extract all assessment components separately with their codes, descriptions, and weight percentages.
- Extract both theory and practice sessions if available.
- For objectives, include outcomeRefs as a list of outcome codes referenced.
- courseCode must be the short code like "IT003", "SE104".
- credits, theoryHours, practiceHours, selfStudyHours are integers.
- weightPercent in assessments is an integer (0-100).

OUTPUT SCHEMA (return exactly this shape):
{
  "courseCode": "string",
  "courseNameVi": "string or null",
  "courseNameEn": "string or null",
  "credits": number or null,
  "theoryHours": number or null,
  "practiceHours": number or null,
  "selfStudyHours": number or null,
  "prerequisite": "string or null",
  "previousCourse": "string or null",
  "department": "string or null",
  "description": "string or null",
  "objectives": [
    {
      "description": "string",
      "outcomeRefs": ["string"]
    }
  ],
  "outcomes": [
    {
      "code": "string",
      "description": "string"
    }
  ],
  "theorySessions": [
    {
      "sessionNo": "string",
      "topic": "string",
      "activities": "string or null",
      "assessmentComponent": "string or null"
    }
  ],
  "practiceSessions": [
    {
      "sessionNo": "string",
      "topic": "string",
      "activities": "string or null",
      "assessmentComponent": "string or null"
    }
  ],
  "assessments": [
    {
      "componentCode": "string",
      "description": "string",
      "weightPercent": number or null
    }
  ],
  "references": ["string"],
  "tools": ["string"]
}""";
    }

    private String buildUserMessage(String markdownContent) {
        return "Extract structured syllabus facts from this markdown document:\n\n" + markdownContent;
    }

    private String stripMarkdownFences(String text) {
        if (text.startsWith("```json")) {
            text = text.substring(7);
        } else if (text.startsWith("```")) {
            text = text.substring(3);
        }
        if (text.endsWith("```")) {
            text = text.substring(0, text.length() - 3);
        }
        return text.trim();
    }
}
