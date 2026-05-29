# User Story: AI Course Q&A Assistant

As a student,
I want to ask the AI assistant questions about courses, syllabi, grading criteria, and study experiences,
So that I can better plan my learning journey and study effectively.

## Acceptance Criteria

- [x] Database is enriched with syllabus fields (`learning_objectives`, `grading_criteria`, `topics`) and chat history tables (`chat_sessions`, `chat_messages`).
- [x] Syllabus seed data parsed and populated into database (from UIT course summary document).
- [x] Backend service orchestrates query analysis, parses course codes, scrapes web context using JSoup, and generates answers using OpenCode Go DeepSeek-v4-flash model.
- [x] Frontend features a premium Glassmorphic floating AI chat widget with suggestions and citations.
- [x] Chat messages parse course codes (like `SE101`) to render clickable CourseBadges.

## Technical Notes

- **Backend**:
  - Flyway migrations `V003` and `V004` to create and populate fields.
  - `SubjectQaService.java` manages searching course syllabus fields, performing web searches, scraping web pages, and querying OpenCode Go API.
  - `SubjectQaController.java` exposes REST endpoint `/api/ai/subject-qa/query`.
- **Frontend**:
  - `AiChatWidget.tsx` floating widget with suggestions and rich citation renderer.
  - `useSubjectQa.ts` react-query hook integrated into `Layout.tsx` for cross-page availability.

## Validation Proof

- **Automated Tests**:
  - Unit tests run and pass.
- **Manual**:
  - Verify course code search yields correct objectives and grading criteria (e.g. "SE101").
  - Verify student general questions (e.g. "làm sao để học tốt môn đại cương giải tích tại UIT") triggers web search and crawl fallback, returning well-reasoned answers citing links.
