# Harness Backlog

Use this file when an agent discovers a missing harness capability but should
not change the operating model immediately.

## Template

```md
## Missing Harness Capability

### Title

Short name.

### Discovered While

Task or story that exposed the gap.

### Current Pain

What was hard, repeated, ambiguous, or unsafe?

### Suggested Improvement

What should be added or changed?

### Risk

Tiny, normal, or high-risk.

### Status

proposed | accepted | implemented | rejected
```

## Items

### Missing Harness Capability

#### Title

Product-feature-aware harness docs

#### Discovered While

Updating docs to reflect DevOrbit codebase state (May 2026)

#### Current Pain

Harness v0 docs describe a pre-implementation state. Real product features
(knowledge graph, photobooth, AI roadmap, mobile) have outgrown the generic
harness descriptions. Agents must cross-reference source code to understand
what actually exists.

#### Suggested Improvement

After each major feature story, generate or update a brief product doc and add
a row to TEST_MATRIX.md. Keep ARCHITECTURE.md synchronized with actual
controller/service/entity counts via periodic audits.

#### Risk

Tiny

#### Status

accepted

---

### Missing Harness Capability

#### Title

Validation command stubs for web frontend

#### Discovered While

Updating TEST_MATRIX.md (May 2026)

#### Current Pain

Web frontend has Vitest configured but zero tests. TEST_MATRIX.md cannot claim
any web coverage. There is no quick check to verify web health.

#### Suggested Improvement

Add at least smoke tests (app renders, routes resolve) and a `npm run test`
command that agents can run for verification.

#### Risk

Normal

#### Status

proposed

