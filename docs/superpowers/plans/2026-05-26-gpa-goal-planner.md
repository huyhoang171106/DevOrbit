# GPA Goal Planner Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a "Toi muon dat GPA X" mode to the existing web GPA calculator that calculates the required current-term GPA and approximate per-course targets.

**Architecture:** Keep the feature client-side inside the existing `/gpa-calculator` route. Add small pure calculation helpers in `GpaCalculatorPage.tsx`, drive them from existing course rows and cumulative inputs, and prove behavior through Vitest render tests.

**Tech Stack:** React 19, TypeScript, Vite 6, Vitest, Testing Library, Tailwind CSS.

---

## Files

- Modify: `devorbit-web/src/pages/student/GpaCalculatorPage.tsx`
  - Extend `CalculationMode` with `goal`.
  - Add `targetGpa` state.
  - Add pure helpers for goal calculation and course target display.
  - Add the third segmented control button and goal input panel.
  - Extend the right-side summary panel for goal mode.
- Modify: `devorbit-web/src/pages/student/GpaCalculatorPage.test.tsx`
  - Add tests for required term GPA, infeasible target, already-above-target, invalid input guidance, and course-level target rows.
- Modify: `docs/product/gpa-calculator.md`
  - Update the product contract with goal-planning behavior and formula.
- Modify: `docs/stories/US-020-gpa-calculator.md`
  - Add acceptance criteria, validation expectations, and evidence for the GPA goal planner update.
- Modify: `docs/TEST_MATRIX.md`
  - Update US-020 contract wording and evidence after validation.
- Modify: `GitNexus/docs/superpowers/specs/2026-05-26-gpa-goal/MILESTONES.md`
  - Append implementation and validation notes.

## Pre-Edit Gate

- [ ] **Step 1: Check worktree state**

Run:

```powershell
git status --short
```

Expected: only known changes from the current task, or unrelated user changes that are not edited.

- [ ] **Step 2: Run GitNexus impact analysis if tools are available**

The AGENTS instructions require impact analysis before editing symbols. Use the GitNexus MCP tool if exposed:

```text
gitnexus_impact({ target: "GpaCalculatorPage", direction: "upstream" })
```

Expected blast radius: direct route rendering and tests for `/gpa-calculator`; risk should be normal. If GitNexus tools are unavailable in the environment, record that in the final response and continue with local file/test inspection.

---

### Task 1: Add Failing Tests For GPA Goal Mode

**Files:**
- Modify: `devorbit-web/src/pages/student/GpaCalculatorPage.test.tsx`

- [ ] **Step 1: Add a helper for selecting goal mode**

Insert this helper after `buttonByText`:

```typescript
function switchToGoalMode() {
  fireEvent.click(buttonByText(/Mục tiêu GPA|Muc tieu GPA/i))
}
```

- [ ] **Step 2: Add the required-term GPA test**

Append this test inside `describe('GpaCalculatorPage', () => { ... })`:

```typescript
test('calculates the required current-term GPA for a target cumulative GPA', () => {
  render(<GpaCalculatorPage />)

  fireEvent.change(input('course-name-1'), { target: { value: 'Nhap mon lap trinh' } })
  fireEvent.change(input('course-credits-1'), { target: { value: '4' } })
  fireEvent.change(input('course-grade-1'), { target: { value: '8' } })
  fireEvent.change(input('course-name-2'), { target: { value: 'Cau truc du lieu' } })
  fireEvent.change(input('course-credits-2'), { target: { value: '3' } })
  fireEvent.change(input('course-grade-2'), { target: { value: '8' } })

  switchToGoalMode()
  fireEvent.change(input('current-gpa'), { target: { value: '7' } })
  fireEvent.change(input('completed-credits'), { target: { value: '20' } })
  fireEvent.change(input('target-gpa'), { target: { value: '7.5' } })

  expect(screen.getByText(/Kỳ này cần trung bình|Ky nay can trung binh/i)).toBeInTheDocument()
  expect(screen.getByText('8.93')).toBeInTheDocument()
  expect(screen.getByText(/Mục tiêu khó nhưng còn khả thi|Muc tieu kho nhung con kha thi/i)).toBeInTheDocument()
  expect(screen.getByText(/Nhap mon lap trinh/i)).toBeInTheDocument()
  expect(screen.getByText(/Cau truc du lieu/i)).toBeInTheDocument()
})
```

- [ ] **Step 3: Add edge-case tests**

Append these tests after the required-term GPA test:

```typescript
test('marks the target as not feasible when required term GPA is above 10', () => {
  render(<GpaCalculatorPage />)

  fireEvent.change(input('course-credits-1'), { target: { value: '3' } })
  fireEvent.change(input('course-grade-1'), { target: { value: '8' } })
  fireEvent.change(input('course-credits-2'), { target: { value: '3' } })
  fireEvent.change(input('course-grade-2'), { target: { value: '8' } })

  switchToGoalMode()
  fireEvent.change(input('current-gpa'), { target: { value: '5' } })
  fireEvent.change(input('completed-credits'), { target: { value: '100' } })
  fireEvent.change(input('target-gpa'), { target: { value: '8' } })

  expect(screen.getByText(/không khả thi|khong kha thi/i)).toBeInTheDocument()
  expect(screen.getByText(/cần cao hơn 10|can cao hon 10/i)).toBeInTheDocument()
})

test('shows already above target when no extra GPA pressure is required', () => {
  render(<GpaCalculatorPage />)

  fireEvent.change(input('course-credits-1'), { target: { value: '4' } })
  fireEvent.change(input('course-grade-1'), { target: { value: '8' } })
  fireEvent.change(input('course-credits-2'), { target: { value: '3' } })
  fireEvent.change(input('course-grade-2'), { target: { value: '8' } })

  switchToGoalMode()
  fireEvent.change(input('current-gpa'), { target: { value: '9' } })
  fireEvent.change(input('completed-credits'), { target: { value: '80' } })
  fireEvent.change(input('target-gpa'), { target: { value: '7' } })

  expect(screen.getByText(/đã vượt mục tiêu|da vuot muc tieu/i)).toBeInTheDocument()
  expect(screen.getByText(/không cần áp lực điểm thêm|khong can ap luc diem them/i)).toBeInTheDocument()
})

test('shows goal guidance when target inputs or term credits are invalid', () => {
  render(<GpaCalculatorPage />)

  fireEvent.change(input('course-credits-1'), { target: { value: '0' } })
  fireEvent.change(input('course-credits-2'), { target: { value: '0' } })

  switchToGoalMode()
  fireEvent.change(input('current-gpa'), { target: { value: '7' } })
  fireEvent.change(input('completed-credits'), { target: { value: '20' } })
  fireEvent.change(input('target-gpa'), { target: { value: '7.5' } })

  expect(screen.getByText(/Nhập tín chỉ kỳ này hợp lệ|Nhap tin chi ky nay hop le/i)).toBeInTheDocument()
})
```

- [ ] **Step 4: Run tests and verify they fail**

Run:

```powershell
npm test -- GpaCalculatorPage
```

Working directory: `devorbit-web`

Expected: FAIL because `Mục tiêu GPA` mode and `target-gpa` input do not exist yet.

---

### Task 2: Add Goal Calculation Helpers

**Files:**
- Modify: `devorbit-web/src/pages/student/GpaCalculatorPage.tsx`

- [ ] **Step 1: Extend types**

Change:

```typescript
type CourseResult = {
  credits: number
  grade10: number
}
```

to:

```typescript
type CourseResult = {
  id: number
  name: string
  credits: number
  grade10: number
}
```

Change:

```typescript
type CalculationMode = 'semester' | 'cumulative'
```

to:

```typescript
type CalculationMode = 'semester' | 'cumulative' | 'goal'
type GoalStatus = 'missing' | 'no-term-credits' | 'already-above-target' | 'not-feasible' | 'difficult' | 'feasible'
```

- [ ] **Step 2: Preserve parsed course identity**

Change `parseCourse` return object from:

```typescript
return {
  credits,
  grade10,
}
```

to:

```typescript
return {
  id: course.id,
  name: course.name.trim() || 'Môn chưa đặt tên',
  credits,
  grade10,
}
```

- [ ] **Step 3: Add goal helper types and functions**

Insert after `formatNumber`:

```typescript
function clampGrade(value: number): number {
  return Math.min(10, Math.max(0, value))
}

function buildGoalSummary(
  currentGpaInput: string,
  completedCreditsInput: string,
  targetGpaInput: string,
  validCourses: CourseResult[],
) {
  const currentGpaValue = Number(currentGpaInput)
  const completedCreditsValue = Number(completedCreditsInput)
  const targetGpaValue = Number(targetGpaInput)
  const termCredits = validCourses.reduce((sum, course) => sum + course.credits, 0)
  const totalCreditsAfterTerm = Number.isFinite(completedCreditsValue) && completedCreditsValue >= 0
    ? completedCreditsValue + termCredits
    : termCredits

  if (termCredits <= 0) {
    return {
      status: 'no-term-credits' as GoalStatus,
      requiredTermGpa: 0,
      targetGpa: targetGpaValue,
      totalCreditsAfterTerm,
      courseTargets: [],
    }
  }

  if (
    !Number.isFinite(currentGpaValue)
    || !Number.isFinite(completedCreditsValue)
    || !Number.isFinite(targetGpaValue)
    || currentGpaValue < 0
    || currentGpaValue > 10
    || completedCreditsValue < 0
    || targetGpaValue < 0
    || targetGpaValue > 10
  ) {
    return {
      status: 'missing' as GoalStatus,
      requiredTermGpa: 0,
      targetGpa: targetGpaValue,
      totalCreditsAfterTerm,
      courseTargets: [],
    }
  }

  const requiredTermGpa = ((targetGpaValue * totalCreditsAfterTerm) - (currentGpaValue * completedCreditsValue)) / termCredits

  const status: GoalStatus = requiredTermGpa > 10
    ? 'not-feasible'
    : requiredTermGpa < 0
      ? 'already-above-target'
      : requiredTermGpa >= 8.5
        ? 'difficult'
        : 'feasible'

  return {
    status,
    requiredTermGpa,
    targetGpa: targetGpaValue,
    totalCreditsAfterTerm,
    courseTargets: validCourses.map((course) => ({
      id: course.id,
      name: course.name,
      credits: course.credits,
      targetGrade: clampGrade(requiredTermGpa),
    })),
  }
}

function goalStatusLabel(status: GoalStatus): string {
  if (status === 'not-feasible') return 'Mục tiêu không khả thi trong kỳ này'
  if (status === 'already-above-target') return 'Bạn đã vượt mục tiêu'
  if (status === 'difficult') return 'Mục tiêu khó nhưng còn khả thi'
  if (status === 'feasible') return 'Mục tiêu khả thi'
  if (status === 'no-term-credits') return 'Nhập tín chỉ kỳ này hợp lệ'
  return 'Nhập đủ GPA hiện tại, tín chỉ và GPA mục tiêu'
}

function goalStatusDescription(status: GoalStatus): string {
  if (status === 'not-feasible') return 'Kỳ này cần cao hơn 10 điểm trung bình, nên mục tiêu này chưa khả thi với số tín chỉ hiện tại.'
  if (status === 'already-above-target') return 'GPA hiện tại đã đủ cao, kỳ này không cần áp lực điểm thêm để giữ mục tiêu đã nhập.'
  if (status === 'difficult') return 'Bạn cần một kỳ học rất mạnh. Hãy ưu tiên các môn nhiều tín chỉ.'
  if (status === 'feasible') return 'Mức điểm cần đạt nằm trong thang 10 và có thể dùng để lập kế hoạch học tập.'
  if (status === 'no-term-credits') return 'Thêm môn hoặc nhập tín chỉ kỳ này hợp lệ để tính ngược GPA mục tiêu.'
  return 'Nhập GPA hiện tại, tín chỉ đã tích lũy và GPA mục tiêu trong khoảng 0 đến 10.'
}
```

- [ ] **Step 4: Add reusable valid courses memo**

Inside `GpaCalculatorPage`, before `summary`, add:

```typescript
const validCourses = useMemo(() => {
  return courses.map(parseCourse).filter((course): course is CourseResult => course !== null)
}, [courses])
```

Then change `summary` from recalculating `validCourses` internally to using the memo:

```typescript
const summary = useMemo(() => {
  const totalCredits = validCourses.reduce((sum, course) => sum + course.credits, 0)

  if (totalCredits === 0) {
    return {
      totalCredits: 0,
      average10: 0,
      classification: 'Nhập tín chỉ và điểm hợp lệ',
    }
  }

  const weighted10 = validCourses.reduce((sum, course) => sum + course.grade10 * course.credits, 0)
  const average10 = weighted10 / totalCredits

  return {
    totalCredits,
    average10,
    classification: classifyGrade10(average10),
  }
}, [validCourses])
```

- [ ] **Step 5: Add target GPA state and goal summary memo**

After `completedCredits` state, add:

```typescript
const [targetGpa, setTargetGpa] = useState('')
```

After `cumulativeSummary`, add:

```typescript
const goalSummary = useMemo(() => {
  return buildGoalSummary(currentGpa, completedCredits, targetGpa, validCourses)
}, [completedCredits, currentGpa, targetGpa, validCourses])
```

- [ ] **Step 6: Run tests and verify helper compile errors are resolved**

Run:

```powershell
npm test -- GpaCalculatorPage
```

Working directory: `devorbit-web`

Expected: still FAIL because UI has not been added, but TypeScript helper errors should not appear.

---

### Task 3: Add Goal Mode UI

**Files:**
- Modify: `devorbit-web/src/pages/student/GpaCalculatorPage.tsx`

- [ ] **Step 1: Add the third segmented control button**

After the cumulative button, insert:

```tsx
<button
  type="button"
  onClick={() => setCalculationMode('goal')}
  className={`h-10 rounded-[6px] px-4 text-[13px] font-bold transition-colors ${
    calculationMode === 'goal'
      ? 'bg-orbit-accent text-zinc-950'
      : 'text-orbit-text-secondary hover:text-orbit-text'
  }`}
>
  Mục tiêu GPA
</button>
```

- [ ] **Step 2: Show cumulative inputs for cumulative and goal modes**

Change:

```tsx
{calculationMode === 'cumulative' && (
```

to:

```tsx
{(calculationMode === 'cumulative' || calculationMode === 'goal') && (
```

- [ ] **Step 3: Add target GPA input inside the existing cumulative/goal panel**

Change the panel grid class from:

```tsx
<div className="grid gap-4 md:grid-cols-2">
```

to:

```tsx
<div className="grid gap-4 md:grid-cols-3">
```

After the completed credits input block, insert:

```tsx
{calculationMode === 'goal' && (
  <div>
    <label className="mb-2 block text-[12px] font-bold uppercase tracking-[0.12em] text-orbit-text-muted" htmlFor="target-gpa">
      GPA mục tiêu
    </label>
    <input
      id="target-gpa"
      type="number"
      min="0"
      max="10"
      step="0.01"
      value={targetGpa}
      onChange={(event) => setTargetGpa(event.target.value)}
      placeholder="8.00"
      className="h-11 w-full rounded-[8px] border border-orbit-border bg-orbit-bg px-3 text-[14px] text-orbit-text outline-none transition-colors placeholder:text-orbit-text-muted focus:border-orbit-accent/60"
    />
  </div>
)}
```

- [ ] **Step 4: Add goal summary rows to the sidebar**

Inside the `<dl>` after the cumulative block, insert:

```tsx
{calculationMode === 'goal' && (
  <>
    <div className="flex items-center justify-between border-t border-orbit-border pt-4">
      <dt className="text-[14px] text-orbit-text-secondary">Kỳ này cần trung bình</dt>
      <dd className="text-[15px] font-bold text-orbit-text">
        {goalSummary.status === 'missing' || goalSummary.status === 'no-term-credits'
          ? '--'
          : formatNumber(clampGrade(goalSummary.requiredTermGpa))}
      </dd>
    </div>
    <div className="flex items-start justify-between gap-4">
      <dt className="text-[14px] text-orbit-text-secondary">Trạng thái mục tiêu</dt>
      <dd className="text-right text-[15px] font-bold text-orbit-text">{goalStatusLabel(goalSummary.status)}</dd>
    </div>
    <div className="flex items-center justify-between">
      <dt className="text-[14px] text-orbit-text-secondary">Tổng tín chỉ sau kỳ này</dt>
      <dd className="text-[15px] font-bold text-orbit-text">{goalSummary.totalCreditsAfterTerm} tín chỉ</dd>
    </div>
  </>
)}
```

- [ ] **Step 5: Add goal status guidance and course targets below the sidebar rows**

After the existing cumulative invalid guidance block, insert:

```tsx
{calculationMode === 'goal' && (
  <div className="mt-4 rounded-[8px] border border-orbit-border bg-orbit-bg p-3 text-[13px] leading-6 text-orbit-text-secondary">
    {goalStatusDescription(goalSummary.status)}
  </div>
)}

{calculationMode === 'goal' && goalSummary.courseTargets.length > 0 && goalSummary.status !== 'missing' && goalSummary.status !== 'no-term-credits' && (
  <div className="mt-4 rounded-[8px] border border-orbit-border bg-orbit-bg p-4">
    <p className="text-[12px] font-black uppercase tracking-[0.14em] text-orbit-text-muted">Gợi ý từng môn</p>
    <div className="mt-3 space-y-3">
      {goalSummary.courseTargets.map((course) => (
        <div key={course.id} className="flex items-center justify-between gap-3 border-b border-orbit-border pb-3 last:border-b-0 last:pb-0">
          <div>
            <p className="text-[13px] font-bold text-orbit-text">{course.name}</p>
            <p className="text-[12px] text-orbit-text-muted">{course.credits} tín chỉ</p>
          </div>
          <p className="text-[15px] font-black text-orbit-accent">{formatNumber(course.targetGrade)}</p>
        </div>
      ))}
    </div>
  </div>
)}
```

- [ ] **Step 6: Update formula note for goal mode**

Change the sidebar formula note text to include target mode:

```tsx
Công thức: tổng của <span className="font-semibold text-orbit-text">điểm x tín chỉ</span> chia cho tổng tín chỉ hợp lệ. Với mục tiêu GPA, app tính ngược điểm trung bình kỳ này cần đạt. Kết quả chỉ mang tính tham khảo.
```

- [ ] **Step 7: Run tests and verify goal tests pass**

Run:

```powershell
npm test -- GpaCalculatorPage
```

Working directory: `devorbit-web`

Expected: PASS for `GpaCalculatorPage` tests.

---

### Task 4: Update Product Docs And Story Evidence

**Files:**
- Modify: `docs/product/gpa-calculator.md`
- Modify: `docs/stories/US-020-gpa-calculator.md`
- Modify: `docs/TEST_MATRIX.md`
- Modify: `GitNexus/docs/superpowers/specs/2026-05-26-gpa-goal/MILESTONES.md`

- [ ] **Step 1: Update product contract**

In `docs/product/gpa-calculator.md`, add these bullets after the cumulative formula bullet:

```markdown
- Goal planning mode accepts current GPA, completed credits, target GPA, and current-term course rows.
- Goal planning formula: `(target GPA * (completed credits + semester credits) - current GPA * completed credits) / semester credits`.
- Goal planning results include the required current-term GPA, feasibility status, total credits after the term, and approximate per-course target grades.
- If the required current-term GPA is above 10, the target is marked not feasible for the current term.
- If the required current-term GPA is below 0, the student is already safely above the target.
```

- [ ] **Step 2: Update story acceptance criteria**

In `docs/stories/US-020-gpa-calculator.md`, add these acceptance criteria after the cumulative GPA criteria:

```markdown
- Students can choose a GPA goal planning mode.
- GPA goal mode asks for current GPA, completed credits, and target GPA.
- GPA goal mode calculates the current-term average needed to reach the target cumulative GPA.
- GPA goal mode marks targets above the 10-point scale as not feasible for the current term.
- GPA goal mode shows approximate per-course target grades from the current valid course rows.
```

- [ ] **Step 3: Update story design notes and validation**

Add this domain rule to the `Design Notes` list:

```markdown
- GPA goal planning uses `(target GPA * (completed credits + semester credits) - current GPA * completed credits) / semester credits`; per-course targets initially use the same required term GPA capped to the 0-10 scale.
```

Replace the Unit proof row with:

```markdown
| Unit | Vitest render tests for weighted 10-point GPA calculation, row add/remove, validation guidance, no 4-point output, semester preset loading, saved learning roadmap assignment precedence, cumulative GPA estimate, goal GPA reverse calculation, infeasible goals, already-above-target goals, and route rendering |
```

- [ ] **Step 4: Update test matrix**

Change the US-020 contract cell in `docs/TEST_MATRIX.md` to:

```markdown
GPA Calculator + Semester Presets + Cumulative Estimate + Goal Planner
```

After validation, update evidence to include:

```markdown
`devorbit-web`: `npm test -- GpaCalculatorPage`; `npm test -- router`; `npm run build`
```

- [ ] **Step 5: Append milestone notes**

Append this section to `GitNexus/docs/superpowers/specs/2026-05-26-gpa-goal/MILESTONES.md`:

```markdown

## Implementation

- Added GPA goal planner mode to the existing GPA calculator route.
- Added reverse GPA calculation, feasibility statuses, and approximate per-course target grades.
- Updated product contract, story evidence, and test matrix.
```

---

### Task 5: Final Validation And Commit

**Files:**
- Validate all modified files.
- Commit all changes.

- [ ] **Step 1: Run focused GPA calculator tests**

Run:

```powershell
npm test -- GpaCalculatorPage
```

Working directory: `devorbit-web`

Expected: PASS.

- [ ] **Step 2: Run route tests**

Run:

```powershell
npm test -- router
```

Working directory: `devorbit-web`

Expected: PASS.

- [ ] **Step 3: Run production build**

Run:

```powershell
npm run build
```

Working directory: `devorbit-web`

Expected: PASS, Vite build completes without TypeScript errors.

- [ ] **Step 4: Run GitNexus change detection if tools are available**

Use the GitNexus MCP tool if exposed:

```text
gitnexus_detect_changes()
```

Expected: changed scope is limited to the GPA calculator page, GPA calculator tests, and related docs. If GitNexus tools are unavailable, record that in the final response.

- [ ] **Step 5: Inspect git diff**

Run:

```powershell
git diff --stat
git diff -- devorbit-web/src/pages/student/GpaCalculatorPage.tsx devorbit-web/src/pages/student/GpaCalculatorPage.test.tsx docs/product/gpa-calculator.md docs/stories/US-020-gpa-calculator.md docs/TEST_MATRIX.md GitNexus/docs/superpowers/specs/2026-05-26-gpa-goal/MILESTONES.md
```

Expected: only GPA goal planner code, tests, and docs are modified.

- [ ] **Step 6: Commit implementation**

Run:

```powershell
git add devorbit-web/src/pages/student/GpaCalculatorPage.tsx devorbit-web/src/pages/student/GpaCalculatorPage.test.tsx docs/product/gpa-calculator.md docs/stories/US-020-gpa-calculator.md docs/TEST_MATRIX.md GitNexus/docs/superpowers/specs/2026-05-26-gpa-goal/MILESTONES.md
git commit -m "feat: add GPA goal planner"
```

Expected: commit succeeds with only the scoped implementation files.

---

## Self-Review

- Spec coverage: The plan covers required term GPA, feasibility states, per-course targets, UI integration, docs, story, test matrix, and validation.
- Placeholder scan: No TBD/TODO/fill-in placeholders remain.
- Type consistency: `CalculationMode`, `GoalStatus`, `CourseResult`, `goalSummary`, and `targetGpa` are defined before use.
- Scope check: The plan does not add backend APIs, persistence, 4-point conversion, mobile behavior, or custom per-course constraints.
