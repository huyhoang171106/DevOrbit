# GPA Calculator UX Improvements Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add row-level validation feedback and quick row-management actions to the existing GPA calculator.

**Architecture:** Keep the feature client-side inside `GpaCalculatorPage.tsx`. Add pure row validation helpers next to `parseCourse`, use them for visual row status and ignored-row summary, and add small row-management handlers for clear, duplicate, add five, and reset.

**Tech Stack:** React 19, TypeScript, Vite 6, Vitest, Testing Library, Tailwind CSS, Lucide icons.

---

## Files

- Modify: `devorbit-web/src/pages/student/GpaCalculatorPage.tsx`
  - Add row validation helper types/functions.
  - Add ignored-row count to the summary memo.
  - Add `duplicateCourse`, `clearCourses`, `addFiveCourses`, and `resetCourses`.
  - Add row warning UI and quick action controls.
- Modify: `devorbit-web/src/pages/student/GpaCalculatorPage.test.tsx`
  - Add tests for row validation and quick actions.
- Modify: `docs/product/gpa-calculator.md`
  - Document row-level validation and quick actions.
- Modify: `docs/stories/US-020-gpa-calculator.md`
  - Add acceptance criteria and validation evidence.
- Modify: `docs/TEST_MATRIX.md`
  - Keep US-020 proof current.
- Modify: `GitNexus/docs/superpowers/specs/2026-05-27-gpa-calculator-ux-improvements/MILESTONES.md`
  - Append implementation and validation notes.

## Pre-Edit Gate

- [ ] **Step 1: Check worktree state**

Run:

```powershell
git status --short
```

Expected: only the spec/plan files from this task before code edits.

- [ ] **Step 2: Run GitNexus impact analysis if tools are available**

Use:

```text
gitnexus_impact({ target: "GpaCalculatorPage", direction: "upstream" })
```

Expected: route-level blast radius for `/gpa-calculator` and related tests. If GitNexus tools are unavailable, record that in the final response and continue with local tests.

## Task 1: Add Failing Tests

**Files:**
- Modify: `devorbit-web/src/pages/student/GpaCalculatorPage.test.tsx`

- [ ] **Step 1: Add tests for row validation**

Append these tests inside `describe('GpaCalculatorPage', () => { ... })`:

```typescript
test('shows row-level validation reasons for invalid course inputs', () => {
  render(<GpaCalculatorPage />)

  fireEvent.change(input('course-credits-1'), { target: { value: '' } })
  fireEvent.change(input('course-grade-1'), { target: { value: '' } })
  fireEvent.change(input('course-credits-2'), { target: { value: '-1' } })
  fireEvent.change(input('course-grade-2'), { target: { value: '11' } })

  expect(screen.getByText(/Nhập tín chỉ|Nhap tin chi/i)).toBeInTheDocument()
  expect(screen.getByText(/Nhập điểm|Nhap diem/i)).toBeInTheDocument()
  expect(screen.getByText(/Tín chỉ phải lớn hơn 0|Tin chi phai lon hon 0/i)).toBeInTheDocument()
  expect(screen.getByText(/Điểm phải từ 0 đến 10|Diem phai tu 0 den 10/i)).toBeInTheDocument()
})

test('shows how many invalid rows are ignored in the summary', () => {
  render(<GpaCalculatorPage />)

  fireEvent.change(input('course-credits-1'), { target: { value: '4' } })
  fireEvent.change(input('course-grade-1'), { target: { value: '8' } })
  fireEvent.change(input('course-credits-2'), { target: { value: '3' } })
  fireEvent.change(input('course-grade-2'), { target: { value: '12' } })

  expect(screen.getByText(/Đang bỏ qua 1 dòng chưa hợp lệ|Dang bo qua 1 dong chua hop le/i)).toBeInTheDocument()
  expect(screen.getByText('8.00')).toBeInTheDocument()
})
```

- [ ] **Step 2: Add tests for quick actions**

Append:

```typescript
test('duplicates a course row directly below the source row', () => {
  render(<GpaCalculatorPage />)

  fireEvent.change(input('course-name-1'), { target: { value: 'Nhap mon lap trinh' } })
  fireEvent.change(input('course-credits-1'), { target: { value: '4' } })
  fireEvent.change(input('course-grade-1'), { target: { value: '8.5' } })
  fireEvent.click(screen.getAllByRole('button', { name: /Nhân bản dòng|Nhan ban dong/i })[0])

  expect(input('course-name-2').value).toBe('Nhap mon lap trinh')
  expect(input('course-credits-2').value).toBe('4')
  expect(input('course-grade-2').value).toBe('8.5')
})

test('supports clear all, add five courses, and reset default quick actions', () => {
  render(<GpaCalculatorPage />)

  fireEvent.click(buttonByText(/Thêm 5 môn|Them 5 mon/i))
  expect(input('course-name-7')).toBeInTheDocument()

  fireEvent.click(buttonByText(/Xóa tất cả|Xoa tat ca/i))
  expect(input('course-name-1')).toBeInTheDocument()
  expect(document.querySelector('#course-name-2')).not.toBeInTheDocument()

  fireEvent.click(buttonByText(/Reset mẫu|Reset mau/i))
  expect(input('course-name-1')).toBeInTheDocument()
  expect(input('course-name-2')).toBeInTheDocument()
  expect(document.querySelector('#course-name-3')).not.toBeInTheDocument()
})
```

- [ ] **Step 3: Run tests to verify RED**

Run:

```powershell
npm test -- GpaCalculatorPage
```

Expected: FAIL because validation messages and quick action buttons do not exist yet.

## Task 2: Implement Validation and Quick Actions

**Files:**
- Modify: `devorbit-web/src/pages/student/GpaCalculatorPage.tsx`

- [ ] **Step 1: Add icon imports**

Change:

```typescript
import { Calculator, Plus, Trash2 } from 'lucide-react'
```

To:

```typescript
import { Calculator, Copy, Plus, RotateCcw, Trash2 } from 'lucide-react'
```

- [ ] **Step 2: Add row validation types and helpers**

Add after `type GoalStatus = ...`:

```typescript
type CourseValidation = {
  creditsReason: string | null
  gradeReason: string | null
  valid: boolean
}
```

Add before `parseCourse`:

```typescript
function validateCourse(course: CourseInput): CourseValidation {
  const creditsText = course.credits.trim()
  const gradeText = course.grade10.trim()
  const credits = Number(creditsText)
  const grade10 = Number(gradeText)
  const creditsReason = creditsText === ''
    ? 'Nhập tín chỉ'
    : !Number.isFinite(credits) || credits <= 0
      ? 'Tín chỉ phải lớn hơn 0'
      : null
  const gradeReason = gradeText === ''
    ? 'Nhập điểm'
    : !Number.isFinite(grade10) || grade10 < 0 || grade10 > 10
      ? 'Điểm phải từ 0 đến 10'
      : null

  return {
    creditsReason,
    gradeReason,
    valid: creditsReason === null && gradeReason === null,
  }
}
```

- [ ] **Step 3: Reuse validation in `parseCourse`**

Change the start of `parseCourse` to:

```typescript
function parseCourse(course: CourseInput): CourseResult | null {
  if (!validateCourse(course).valid) return null

  const credits = Number(course.credits)
  const grade10 = Number(course.grade10)
```

- [ ] **Step 4: Add ignored row count to summary**

Inside `summary` memo, compute parsed courses and invalid count:

```typescript
const parsedCourses = courses.map((course) => ({
  course: parseCourse(course),
  validation: validateCourse(course),
}))
const validCourses = parsedCourses.map((item) => item.course).filter((course): course is CourseResult => course !== null)
const ignoredRows = parsedCourses.filter((item) => !item.validation.valid).length
```

Return `ignoredRows` from both summary branches.

- [ ] **Step 5: Add row management helpers**

Add after `addCourse`:

```typescript
const addFiveCourses = () => {
  setCourses((current) => {
    const startId = Math.max(...current.map((course) => course.id)) + 1
    return [
      ...current,
      ...Array.from({ length: 5 }, (_, index) => ({
        id: startId + index,
        name: '',
        credits: '3',
        grade10: '',
      })),
    ]
  })
}

const clearCourses = () => {
  setCourses([{ id: 1, name: '', credits: '', grade10: '' }])
}

const resetCourses = () => {
  setCourses(initialCourses)
}

const duplicateCourse = (id: number) => {
  setCourses((current) => {
    const index = current.findIndex((course) => course.id === id)
    if (index === -1) return current
    const nextId = Math.max(...current.map((course) => course.id)) + 1
    const source = current[index]
    return [
      ...current.slice(0, index + 1),
      { ...source, id: nextId },
      ...current.slice(index + 1),
    ]
  })
}
```

- [ ] **Step 6: Add quick actions and row validation UI**

Add compact quick action buttons beside the existing add button. For each rendered row, call `const validation = validateCourse(course)` and apply warning classes to invalid inputs. Add text below credits/grade inputs when `validation.creditsReason` or `validation.gradeReason` exists. Add a copy icon button with `aria-label="Nhân bản dòng"`.

- [ ] **Step 7: Add ignored-row summary notice**

Render below the `dl` summary:

```tsx
{summary.ignoredRows > 0 && (
  <p className="mt-4 rounded-[8px] border border-amber-300/30 bg-amber-300/10 p-3 text-[13px] leading-6 text-amber-100">
    Đang bỏ qua {summary.ignoredRows} dòng chưa hợp lệ.
  </p>
)}
```

- [ ] **Step 8: Run tests to verify GREEN**

Run:

```powershell
npm test -- GpaCalculatorPage
```

Expected: PASS.

## Task 3: Update Harness Docs

**Files:**
- Modify: `docs/product/gpa-calculator.md`
- Modify: `docs/stories/US-020-gpa-calculator.md`
- Modify: `docs/TEST_MATRIX.md`
- Modify: `GitNexus/docs/superpowers/specs/2026-05-27-gpa-calculator-ux-improvements/MILESTONES.md`

- [ ] **Step 1: Update product contract**

Add bullets for row-level validation and quick actions.

- [ ] **Step 2: Update story acceptance criteria and validation**

Add acceptance criteria for row reasons, ignored row notice, duplicate, clear all, add five, and reset default.

- [ ] **Step 3: Update milestone**

Append implementation and validation notes.

## Task 4: Final Verification

- [ ] **Step 1: Run focused route tests**

```powershell
npm test -- GpaCalculatorPage router
```

Expected: PASS.

- [ ] **Step 2: Run frontend test suite**

```powershell
npm test
```

Expected: PASS.

- [ ] **Step 3: Run production build**

```powershell
npm run build
```

Expected: PASS.

- [ ] **Step 4: Review changed files**

```powershell
git diff --stat
git diff -- devorbit-web/src/pages/student/GpaCalculatorPage.tsx devorbit-web/src/pages/student/GpaCalculatorPage.test.tsx docs/product/gpa-calculator.md docs/stories/US-020-gpa-calculator.md docs/TEST_MATRIX.md GitNexus/docs/superpowers/specs/2026-05-27-gpa-calculator-ux-improvements/MILESTONES.md
```

Expected: only GPA calculator UX code, tests, and docs are modified.

## Self-Review

- Spec coverage: row validation, ignored row count, duplicate row, clear all, add five, reset default, docs, and validation are covered.
- Placeholder scan: no placeholders remain.
- Type consistency: helper names and file paths match the planned implementation.
