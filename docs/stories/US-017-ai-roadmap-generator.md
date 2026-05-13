# User Story: AI Roadmap Generator

As a student,
I want to generate a personalized knowledge graph based on my career goals,
So that I can see which elective courses are most relevant to my future.

## Acceptance Criteria

- [ ] Static TC (synthetic) nodes are removed from the default knowledge graph.
- [ ] Knowledge graph nodes handle single-click (select) and double-click (navigate to /courses/:id).
- [ ] A new UI component allows users to enter "Learning Goals" and "Career Path".
- [ ] The system calls an LLM to generate course recommendations based on user input.
- [ ] Recommendations include detailed reasoning and course descriptions.
- [ ] Recommendations are selected from the existing database (matching course names and descriptions).

## Technical Notes

- **Backend**:
  - Update `KnowledgeGraphService.java` to filter out synthetic TC nodes.
  - Add `generateRoadmap` endpoint to `AiService.java` and `PublicAiController.java`.
  - Prompt logic should extract relevant courses from the `CourseRepository`.
- **Frontend**:
  - Update `GalaxyPage.tsx` for interaction fixes and node filtering.
  - Implement `AIInputPanel` component.
  - Integrate with the new AI endpoint.

## Validation Proof

- **Manual**:
  - Verify TC nodes are gone from `/knowledge-graph`.
  - Verify single-click selects a node (highlight).
  - Verify double-click redirects to course detail.
  - Verify AI generation returns well-reasoned recommendations.
