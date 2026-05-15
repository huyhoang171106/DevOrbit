# Stories

Stories are work packets. They turn product intent into bounded implementation
and validation work.

## Active Stories

| File | Status | Description |
|------|--------|-------------|
| `simulation-engine.md` | implemented | Simulation mode for knowledge graph (failure cascade) |
| `photobooth-upload.md` | implemented | Photobooth upload and frame compositing |
| `US-017-ai-roadmap-generator.md` | implemented | AI Roadmap Generator endpoint |
| `mobile-repo-tech-stack-filter.md` | implemented | Mobile repo tech stack filter UI |
| `mobile-course-hub-detail-navigation.md` | implemented | Mobile course hub detail navigation |
| `explore-screen-enhancement.md` | planned | Mobile explore screen enhancement |
| `mobile-subject-screen-enhancement.md` | planned | Mobile subject screen enhancement |
| `SE-2025-Curriculum-Integration.md` | implemented | SE2025 curriculum data and knowledge graph |
| `redesign/` | in_progress | UI redesign (mobile) |

## Normal Story

Use `docs/templates/story.md` for normal feature work.

Suggested path:

```text
docs/stories/epics/E01-domain-name/US-001-short-story-title.md
```

## High-Risk Story

Use `docs/templates/high-risk-story/` when the feature intake classifies work as
high-risk.

Suggested path:

```text
docs/stories/epics/E02-risky-domain/US-012-risky-story-title/
  execplan.md
  overview.md
  design.md
  validation.md
```

## Status Flow

```text
planned -> in_progress -> implemented
                  |
                  v
               changed
                  |
                  v
               retired
```
