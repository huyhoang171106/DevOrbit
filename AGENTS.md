# Agent Operating Guide

This repository contains a full-stack production application (**DevOrbit**)
alongside the **Harness v0** collaboration framework.

- **Product**: DevOrbit — a multi-platform system for managing and discovering
  academic source code, knowledge graphs, AI photobooth, and AI roadmaps for
  UIT students. Built with Spring Boot 4 (Java 21), React 19 (Vite 6), and
  Kotlin Jetpack Compose.
- **Harness**: Harness v0 — the human-agent operating model for safe,
  validated feature work. The harness was built first; product code was built
  on top of it.

Agent work splits into two categories:

1. **Product work** — implementing features, fixing bugs, updating routes,
   adding entities, writing API endpoints, modifying React components.
2. **Harness work** — improving how humans and agents collaborate (docs,
   templates, validation expectations, decision records).

Do not create product scaffolding unless a story explicitly requires it.
Do not break the harness when making product changes.

## Source Of Truth

Read in this order:

1. `README.md` for project status.
2. `docs/HARNESS.md` for the human-agent operating model (GSD methodology — defines the interaction loop, task classification, risk lanes, and done definition).
3. `docs/FEATURE_INTAKE.md` before turning any prompt into work (GSD methodology — classifies requests as spec slices, change requests, maintenance, or harness improvements).
4. The user-provided spec or prompt, when one exists.
5. `docs/product/` for current product contracts.
6. `docs/ARCHITECTURE.md` before proposing implementation shape.
7. `docs/stories/` for story packets and backlog.
8. `docs/TEST_MATRIX.md` for proof status.
9. `docs/decisions/` for why important choices were made.

This harness does not ship with a project-specific `SPEC.md`. When the human
provides a spec for a new project, treat that spec as input material for the
first buildout. Derive product docs, story packets, architecture decisions, and
validation expectations from it. Product docs, stories, tests, and decisions
then become the living contract that agents should update as the system evolves.

## Interaction Loop

For every human interaction, follow this sequence before, during, and after the
task loop.

### 1. User Identification

- Assume the interacting user is `default_user` unless evidence says otherwise.
- If the user has not been identified (no entity exists in the knowledge graph
  for them), proactively determine their identity through available context.

### 2. Memory Retrieval

- At the start of every interaction, say only: `Remembering...`
- Immediately retrieve all relevant information from the knowledge graph
  (referred to as your "memory").

### 3. Memory Awareness During Interaction

- While conversing, be attentive to any new information that falls into these
  categories:
  a) **Basic Identity** — age, gender, location, job title, education level, etc.
  b) **Behaviors** — interests, habits, recurring actions.
  c) **Preferences** — communication style, preferred language, tooling choices.
  d) **Goals** — targets, aspirations, project objectives.
  e) **Relationships** — personal and professional relationships up to 3 degrees
     of separation.

### 4. Memory Update

- If new information was gathered during the interaction, update the knowledge
  graph as follows:
  a) Create entities for recurring organizations, people, and significant events.
  b) Connect them to existing entities using relations.
  c) Store facts about them as observations.

## Task Loop

For every task:

1. Classify the request with `docs/FEATURE_INTAKE.md`.
2. Identify whether the input is a new spec, spec slice, change request, new
   initiative, maintenance request, or harness improvement.
3. Locate the affected product docs and story files.
4. Check `docs/TEST_MATRIX.md` for existing proof and gaps.
5. Work only inside the selected lane: tiny, normal, or high-risk.
6. Before finishing, ask:
   - Did product truth change?
   - Did validation expectations change?
   - Did architecture rules change?
   - Did we discover a repeated failure pattern?
   - Did the next agent need a clearer instruction?
7. Update routine harness files directly, or add a proposal to
   `docs/HARNESS_BACKLOG.md` when the change is structural.

## Harness Change Policy

Agents may update directly:

- Story status and evidence.
- `docs/TEST_MATRIX.md` rows.
- Links from story packets to product docs.
- Validation notes and reports.
- Small clarifications tied to the current task.

Agents should ask for human confirmation before:

- Changing architecture direction.
- Removing validation requirements.
- Changing the source-of-truth hierarchy.
- Changing risk classification rules.
- Replacing the feature workflow.

## Done Definition

A task is done only when:

- The requested change is completed or the blocker is documented.
- Relevant docs, stories, and test matrix entries remain current.
- Validation commands were run when they exist.
- Tests were run after every code edit.
- Missing harness capabilities were added to `docs/HARNESS_BACKLOG.md`.
- The final response says what changed and what was not attempted.

## CI/CD Pre-Push Gate

**NEVER push without passing all CI/CD checks locally first.**

Before any `git push`, run ALL of these locally and verify they pass:

```bash
# 1. API compile check
cd devorbit-api && ./mvnw compile -B

# 2. Web TypeScript check + tests
cd devorbit-web && npx tsc --noEmit && npm test

# 3. Verify workflow file is valid YAML (no syntax errors)
yq . .github/workflows/ci.yml > /dev/null 2>&1 || echo "Invalid YAML"
```

If any check fails:
- Fix the issue before pushing.
- Never push with the intention of "CI will tell me what's wrong."
- CI failures waste runner time and block other PRs.

**After pushing**, verify CI passes:
```bash
gh run watch --repo huyhoang171106/DevOrbit --exit-status
```

If CI fails after a local pass:
- Check for environment differences (Node version, OS, cached deps).
- Fix and push again.
- Never merge a failing PR.

<!-- codegraph:start -->
# CodeGraph — Code Intelligence

This project is indexed by CodeGraph (v0.9.4) as **DevOrbit** (4,651 nodes, 8,131 edges across 454 files). Use CodeGraph pi tools to understand code, assess impact, and navigate safely.

> If `codegraph_status` warns the index is stale, run `codegraph_sync` first.

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `codegraph_impact({symbol: "symbolName"})` and report the blast radius (direct callers, affected files, risk level) to the user.
- **MUST run `codegraph_sync` after editing and before querying** to keep the index in sync with file changes.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `codegraph_query({search: "concept"})` to find symbols instead of grepping. It returns ranked results by relevance.
- When you need full context on a specific symbol — callers, callees, related files — use `codegraph_context({task: "symbolName"})`.

## Never Do

- NEVER edit a function, class, or method without first running `codegraph_impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace — trace the call graph first.
- NEVER commit changes without running `codegraph_sync` to verify clean state.

## Resources

| Resource | Use for |
|----------|---------|
| `codegraph_status` | Codebase overview, check index freshness |
| `codegraph_query` | Find symbols and declarations |
| `codegraph_context` | Build task-focused context |
| `codegraph_impact` | Multi-level blast radius analysis |

## Skills

| Task | Read this skill file |
|------|---------------------|
| Understand architecture / "How does X work?" | `.agents/skills/codegraph-exploring/SKILL.md` |
| Blast radius / "What breaks if I change X?" | `.agents/skills/codegraph-impact-analysis/SKILL.md` |
| Trace bugs / "Why is X failing?" | `.agents/skills/codegraph-debugging/SKILL.md` |
| Rename / extract / split / refactor | `.agents/skills/codegraph-refactoring/SKILL.md` |
| Tools, resources, schema reference | `.agents/skills/codegraph-guide/SKILL.md` |
| Index, status, clean, CLI commands | `.agents/skills/codegraph-cli/SKILL.md` |

<!-- codegraph:end -->

Spec directories live under `docs/superpowers/specs` unless a nested AGENTS.md documents a more specific convention.
Spec directory names use `YYYY-MM-DD-kebab-feature`, for example `2026-05-01-spec-lifecycle-audit`.
Spec directories include a free-form `MILESTONES.md` implementation log for milestones, setbacks, fixes, validation notes, and decisions.

## Codebase Wiki

This project has an auto-maintained knowledge base at `.codebase-wiki/` (run `/wiki-init` to initialize if not present).

### Keeping the Wiki Updated

- **After making code changes**, run `wiki_ingest` with source `commits` or `smart` to update affected pages.
- **After refactoring or adding modules**, run `wiki_ingest` with source `tree` to sync the file tree.
- **Periodically run `wiki_lint`** to catch contradictions, orphans, and stale pages.
- **When you create an ADR or major design decision**, use `wiki_decision` to record it.
- **When you add a cross-cutting pattern**, use `wiki_concept` to document it.
- **When you need context**, use `wiki_query` instead of grepping source files.

### Wiki Page Types

| Type | Directory | Purpose |
|------|-----------|---------|
| entity | `entities/` | Code modules, services, and components |
| concept | `concepts/` | Cross-cutting patterns and architectural themes |
| decision | `decisions/` | Architecture Decision Records (ADRs) |
| evolution | `evolution/` | Feature change history traced from git |
| query | `queries/` | Filed search queries for cross-referencing |

### Workflow

1. **Initialize**: `/wiki-init` creates `.codebase-wiki/` with SCHEMA.md, templates, and INDEX.md.
2. **Populate**: `wiki_ingest` with `tree` (initial), `commits` (incremental), `smart` (enrich), or `llm` (agent-written).
3. **Query**: `wiki_query` searches pages and files good queries back as new wiki pages.
4. **Lint**: `wiki_lint` checks for contradictions, orphans, stale pages, broken links.
5. **Evolve**: `wiki_evolve` traces how a feature changed over time from git history.

Pages are tracked in SQLite (`.codebase-wiki/meta/wiki.db`) and versioned in git.
Edit pages by hand or via tools — the wiki respects hand-edited content and won't overwrite it with stubs.
