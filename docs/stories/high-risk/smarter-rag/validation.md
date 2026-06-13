# Validation

## Proof Strategy

Unit tests for new components (RagQueryPlanner, RagResultReranker), updated service tests for hybrid flow, fallback paths, adaptive web gate, indexing changes. Focused Maven test runs, then full suite.

## Test Plan

| Layer | Cases |
| --- | --- |
| Unit | RagQueryPlannerTest, RagResultRerankerTest |
| Unit | KnowledgeRetrievalServiceTest (hybrid + fallback) |
| Unit | CourseKnowledgeIndexerTest (summary chunks, overlap, parent IDs) |
| Unit | SubjectQaServiceTest (adaptive web fallback, skip when good RAG) |
| Unit | TutorRagServiceTest (score display, empty hybrid) |
| Unit | WebSearchServiceTest (fallback, both disabled, noise filter) |
| Integration | (full Maven suite after H2/vector blocker fix) |

## Fixtures

Mockito mocks for all tests. No database fixtures needed for unit tests.

## Commands

```bat
.\mvnw.cmd -Dtest=RagQueryPlannerTest,RagResultRerankerTest,KnowledgeRetrievalServiceTest,CourseKnowledgeIndexerTest test -B
.\mvnw.cmd -Dtest=SubjectQaServiceTest,TutorRagServiceTest,WebSearchServiceTest test -B
.\mvnw.cmd test -B
.\mvnw.cmd compile -B
```

## Acceptance Evidence

TBD after verification steps are run.
