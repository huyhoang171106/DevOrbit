# <TÃŠN PROJECT>

> File nÃ y load má»i session khi lÃ m project nÃ y. Cá»™ng dá»“n vá»›i `~/.claude/CLAUDE.md` global. Chá»‰ chá»©a thá»© RIÃŠNG project, KHÃ”NG láº·p láº¡i global. Giá»¯ <80 dÃ²ng.

## MÃ´ táº£ project

<1-2 cÃ¢u: project nÃ y lÃ m gÃ¬, dÃ¹ng cho ai, á»Ÿ stage nÃ o (MVP / production / maintenance).>

## Tech stack

- **Language**: <vd: TypeScript 5.x, Python 3.12, Go 1.22>
- **Framework**: <vd: Next.js 14 / NestJS / FastAPI / Spring Boot>
- **Database**: <vd: PostgreSQL 16, Redis 7>
- **Test**: <vd: Vitest, pytest, Go test>
- **Build**: <vd: Vite, esbuild, Maven>
- **Deploy**: <vd: Vercel, AWS ECS, GCP Cloud Run>

## Lá»‡nh quan trá»ng

```bash
<command-cÃ i-deps>           # vd: pnpm install
<command-dev>                 # vd: pnpm dev
<command-test>                # vd: pnpm test
<command-test-watch>          # vd: pnpm test:watch
<command-lint>                # vd: pnpm lint
<command-typecheck>           # vd: pnpm typecheck
<command-format>              # vd: pnpm format
<command-build>               # vd: pnpm build
<command-migrate-db>          # vd: pnpm db:migrate
```

## Cáº¥u trÃºc thÆ° má»¥c

```text
src/
â”œâ”€â”€ <module-1>/   # mÃ´ táº£ ngáº¯n
â”œâ”€â”€ <module-2>/   # mÃ´ táº£ ngáº¯n
â””â”€â”€ ...
tests/
docs/
```

<Chá»‰ note thÆ° má»¥c cÃ³ quy Æ°á»›c SPECIAL â€” khÃ´ng list tá»«ng folder.>

## Convention RIÃŠNG project

<Chá»‰ ghi nhá»¯ng convention KHÃC global, hoáº·c cá»¥ thá»ƒ project nÃ y. Vd:>
- Táº¥t cáº£ API endpoint tráº£ vá» `{ data, error, meta }`, khÃ´ng bare object.
- TÃªn file React component: `PascalCase.tsx`. Hook: `useCamelCase.ts`.
- DB column: `snake_case`. JS variable: `camelCase`. Model class: `PascalCase`.
- Táº¥t cáº£ `useState` cho async data â†’ dÃ¹ng React Query thay tháº¿.

## Module owner & nÆ¡i cáº§n cáº©n tháº­n

- `src/auth/*` â€” security sensitive, cáº§n test trÆ°á»›c khi sá»­a.
- `src/payment/*` â€” KHÃ”NG sá»­a khi khÃ´ng cÃ³ ticket. Cháº¡m code nÃ y pháº£i cÃ³ review @<owner>.
- `src/migration/*` â€” chá»‰ thÃªm migration má»›i, KHÃ”NG sá»­a migration cÅ© Ä‘Ã£ apply.

## VÃ¹ng cáº¥m / Ä‘iá»u cáº§n biáº¿t

- KHÃ”NG Ä‘á»•i schema DB khÃ´ng cÃ³ migration file kÃ¨m.
- KHÃ”NG `git push --force` lÃªn `main`/`develop`.
- KHÃ”NG bypass pre-commit hook (lint/test pháº£i pass).
- API breaking change pháº£i bump major version + cáº­p nháº­t `CHANGELOG.md`.

## Compact Instructions (cho `/compact` cáº£ manual vÃ  auto)

Khi compact, summary PHáº¢I giá»¯:
1. **File Ä‘Ã£ sá»­a** trong session (full path) + lÃ½ do tá»«ng file.
2. **Migration Ä‘Ã£ cháº¡y** / dependency Ä‘Ã£ thÃªm.
3. **Quyáº¿t Ä‘á»‹nh kiáº¿n trÃºc** Ä‘Ã£ chá»‘t (kÃ¨m rationale 1 cÃ¢u).
4. **BÆ°á»›c Ä‘ang dá»Ÿ** + bÆ°á»›c tiáº¿p theo cá»¥ thá»ƒ.
5. **Bug Ä‘Ã£ reproduce** nhÆ°ng chÆ°a fix.
Bá» qua: tool output dÃ i, build log, dead-end debugging.

## Tham chiáº¿u

- Docs project: <link Notion / Confluence / Wiki>
- Design system: <link Figma>
- API spec: <link Swagger / Postman>
- Deployment runbook: <link>

<!-- gitnexus:start -->
# GitNexus â€” Code Intelligence

This project is indexed by GitNexus as **DevOrbit** (33240 symbols, 47366 relationships, 300 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

> If any GitNexus tool warns the index is stale, run `npx gitnexus analyze` in terminal first.

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `gitnexus_impact({target: "symbolName", direction: "upstream"})` and report the blast radius (direct callers, affected processes, risk level) to the user.
- **MUST run `gitnexus_detect_changes()` before committing** to verify your changes only affect expected symbols and execution flows.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `gitnexus_query({query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol â€” callers, callees, which execution flows it participates in â€” use `gitnexus_context({name: "symbolName"})`.

## Never Do

- NEVER edit a function, class, or method without first running `gitnexus_impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace â€” use `gitnexus_rename` which understands the call graph.
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

<!-- cortex-hub:auto-mcp -->
## Cortex Hub â€” MANDATORY (enforced by hooks)

**YOUR FIRST ACTION in every conversation MUST be calling ``cortex_session_start``.**
If you skip this, all Edit/Write/file-modifying Bash commands will return exit code 2 (BLOCKED).

``cortex_session_start(repo: "https://github.com/huyhoang171106/DevOrbit.git", mode: "development", agentId: "claude-code")``

Then:
- If ``recentChanges.count > 0`` - warn user and ``git pull``
- Read ``STATE.md`` if it exists

### Quality gates (enforced - commit blocked without these)
Run verify commands from ``.cortex/project-profile.json``.
Call ``cortex_quality_report`` then ``cortex_session_end``.
<!-- cortex-hub:auto-mcp -->

