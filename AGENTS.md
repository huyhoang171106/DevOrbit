# Agent Operating Guide

This repository is in Harness v0. There is no product implementation yet.

The current job of agents is to preserve and grow the collaboration harness
before writing application code. Do not scaffold application source folders,
platform shells, package scripts, CI, or tests unless a later story explicitly
moves the project into implementation.

## Source Of Truth

Read in this order:

1. `README.md` for project status.
2. `docs/HARNESS.md` for the human-agent operating model.
3. `docs/FEATURE_INTAKE.md` before turning any prompt into work.
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

<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **DevOrbit** (33240 symbols, 47366 relationships, 300 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

> If any GitNexus tool warns the index is stale, run `npx gitnexus analyze` in terminal first.

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `gitnexus_impact({target: "symbolName", direction: "upstream"})` and report the blast radius (direct callers, affected processes, risk level) to the user.
- **MUST run `gitnexus_detect_changes()` before committing** to verify your changes only affect expected symbols and execution flows.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `gitnexus_query({query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol — callers, callees, which execution flows it participates in — use `gitnexus_context({name: "symbolName"})`.

## Never Do

- NEVER edit a function, class, or method without first running `gitnexus_impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace — use `gitnexus_rename` which understands the call graph.
- NEVER commit changes without running `gitnexus_detect_changes()` to check affected scope.

## Resources

| Resource | Use for |
|----------|---------|
| `gitnexus://repo/DevOrbit/context` | Codebase overview, check index freshness |
| `gitnexus://repo/DevOrbit/clusters` | All functional areas |
| `gitnexus://repo/DevOrbit/processes` | All execution flows |
| `gitnexus://repo/DevOrbit/process/{name}` | Step-by-step execution trace |

## CLI

| Task | Read this skill file |
|------|---------------------|
| Understand architecture / "How does X work?" | `.claude/skills/gitnexus/gitnexus-exploring/SKILL.md` |
| Blast radius / "What breaks if I change X?" | `.claude/skills/gitnexus/gitnexus-impact-analysis/SKILL.md` |
| Trace bugs / "Why is X failing?" | `.claude/skills/gitnexus/gitnexus-debugging/SKILL.md` |
| Rename / extract / split / refactor | `.claude/skills/gitnexus/gitnexus-refactoring/SKILL.md` |
| Tools, resources, schema reference | `.claude/skills/gitnexus/gitnexus-guide/SKILL.md` |
| Index, status, clean, wiki CLI commands | `.claude/skills/gitnexus/gitnexus-cli/SKILL.md` |

<!-- gitnexus:end -->

Spec directories live under `GitNexus\docs\superpowers\specs` unless a nested AGENTS.md documents a more specific convention.
Spec directory names use `YYYY-MM-DD-kebab-feature`, for example `2026-05-01-spec-lifecycle-audit`.
Spec directories include a free-form `MILESTONES.md` implementation log for milestones, setbacks, fixes, validation notes, and decisions.
