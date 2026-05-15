# GSD-STYLE.md

> **Comprehensive reference for DevOrbit.**

This document explains how DevOrbit documentation and plans are written so AI
agents can contribute consistently.

## Core Philosophy

DevOrbit uses GSD methodology adapted for a full-stack production application.
The system optimizes for:

- **Solo developer + AI workflow** (no enterprise patterns)
- **Context engineering** (manage the context window deliberately)
- **Plans as executable prompts** (PLAN.md / story files are executed directly)

---

## File Structure Conventions

### Workflows

Project workflows are defined in `AGENTS.md` at the project root. Key workflows:

- **Feature intake**: Classify prompts using `docs/FEATURE_INTAKE.md`
- **Story packets**: Create/update stories under `docs/stories/`
- **Implementation**: Edit source files in `devorbit-api/`, `devorbit-web/`, etc.
- **Validation**: Update `docs/TEST_MATRIX.md` with proof

### Templates (`.docs/templates/`)

Reusable document structures for stories, decisions, specs, and validation reports.

### Stories (`docs/stories/`)

Active and planned story packets describing bounded feature work.

---

## XML Tag Conventions

### Semantic Containers Only

Use XML tags for semantic meaning, not formatting:

```markdown
<role>
You are a GSD executor...
</role>

<objective>
Execute all plans in a phase...
</objective>

<process>
## 1. First Step
...
</process>
```

### Task Structure

```xml
<task type="auto" effort="medium">
  <name>Clear descriptive name</name>
  <files>exact/path/to/file.ts</files>
  <action>
    Specific implementation instructions.
    AVOID: common mistake (reason)
    USE: preferred approach (reason)
  </action>
  <verify>executable command that proves completion</verify>
  <done>measurable acceptance criteria</done>
</task>
```

### Effort Attribute (Optional)

The `effort` attribute hints at task complexity for model selection:

| Value | Use Case | Model Hint |
|-------|----------|------------|
| `low` | Simple edits, formatting | Fast models |
| `medium` | Standard implementation (default) | Standard models |
| `high` | Complex logic, refactoring | Reasoning models |
| `max` | Architecture, security-critical | Deep reasoning |

**Default:** `medium` if omitted. No workflow should fail if this attribute is absent.

See [docs/model-selection-playbook.md](docs/model-selection-playbook.md) for model selection guidance.


### Checkpoint Structure

```xml
<task type="checkpoint:human-verify">
  <name>Verify UI renders correctly</name>
  <action>User reviews the rendered component</action>
  <verify>User confirms visual correctness</verify>
</task>
```

---

## Language & Tone

### Imperative Voice
- ✅ "Create the file"
- ❌ "You should create the file"
- ❌ "We will create the file"

### No Filler
- ✅ "Run `npm test`"
- ❌ "Now let's go ahead and run `npm test`"

### No Sycophancy
- ✅ "Phase complete."
- ❌ "Great job! Phase complete!"

### Brevity with Substance
Every sentence should convey information. Remove words that don't add meaning.

---

## Context Engineering

### Size Constraints

| Context Usage | Quality |
|---------------|---------|
| 0-30% | PEAK — Thorough, comprehensive |
| 30-50% | GOOD — Confident, solid work |
| 50-70% | DEGRADING — Efficiency mode begins |
| 70%+ | POOR — Rushed, minimal |

**Rule:** Plans should complete within ~50% context.

### Fresh Context Pattern

When spawning subprocesses (plans, tasks), they get:
- The specific plan being executed
- Minimal necessary context from parent files
- NO accumulated orchestrator state

### State Preservation

STATE.md exists because context windows are temporary.
Everything important goes in STATE.md so the next session can continue.

---

## Anti-Patterns to Avoid

### Enterprise Patterns (Banned)

❌ Stakeholder communication
❌ Team coordination
❌ Sprint ceremonies
❌ Multiple approval levels
❌ Separate environments requiring explicit promotion

### Temporal Language (Banned in Implementation Docs)

❌ "First, we'll..."
❌ "Next, we should..."
❌ "Finally, we'll..."

### Generic XML (Banned)

```xml
<!-- DON'T -->
<section>
  <title>Authentication</title>
  <content>...</content>
</section>

<!-- DO -->
<task type="auto">
  <name>Create login endpoint</name>
  ...
</task>
```

### Vague Tasks (Banned)

```xml
<!-- DON'T -->
<action>Implement authentication</action>

<!-- DO -->
<action>
  Create POST /api/auth/login endpoint.
  Accept {email, password} JSON body.
  Query User by email, compare with bcrypt.
  Return JWT in httpOnly cookie on success.
  Return 401 on failure.
</action>
```

---

## Commit Conventions

### Format
```
type(scope): description
```

### Types
| Type | Use For |
|------|---------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation |
| `refactor` | Code restructure |
| `test` | Add tests |
| `chore` | Maintenance |

### Rules
- One task = one commit
- Scope is phase number for phase work
- No commit before verification passes

---

## UX Patterns

### Banners

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 GSD ► STATUS MESSAGE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### "Next Up" Format

```
───────────────────────────────────────────────────────

▶ NEXT

/command — description

───────────────────────────────────────────────────────
```

### Decision Gates

When user input needed:
```
⚠️ DECISION REQUIRED

Option A: {description}
Option B: {description}

Which do you prefer?
```

---

## Summary: Core Meta-Patterns

1. **Plans are prompts** — PLAN.md is read and executed directly
2. **Fresh context for execution** — Each plan runs in clean context
3. **STATE.md is memory** — Everything important persists there
4. **Verify before done** — No "trust me, it works"
5. **Aggressive atomicity** — Small tasks, atomic commits
6. **No enterprise theater** — Solo dev + AI workflow only
