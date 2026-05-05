# Optimize GitHub Scan UX Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Chuyển đổi từ việc nhập mã Course ID thủ công sang chọn từ danh sách, đồng thời tự động gợi ý Search Query thông minh dựa trên môn học đã chọn.

**Architecture:** Nâng cấp React component `ScanForm` để fetch danh sách môn học từ API, sử dụng state để quản lý việc lựa chọn và hiển thị gợi ý (pills).

**Tech Stack:** React (Frontend), Spring Boot (Backend API), GitHub Search API.

---

### Task 1: Fetch and Display Courses in ScanForm

**Files:**
- Modify: `devorbit-web/src/components/admin/ScanForm.tsx`

**Step 1: Update state and fetch courses**
- Import `useEffect` and `apiGet`.
- Add `courses` and `loadingCourses` state.
- Fetch from `/api/courses` on mount.

**Step 2: Replace Input with Select**
- Replace `input type="number"` for courseId with a `<select>` element.
- Map through `courses` to render options.

**Step 3: Commit**
```bash
git add devorbit-web/src/components/admin/ScanForm.tsx
git commit -m "feat: replace course id input with dropdown in ScanForm"
```

---

### Task 2: Implement Smart Search Query Suggestions

**Files:**
- Modify: `devorbit-web/src/components/admin/ScanForm.tsx`

**Step 1: Add Suggestion Logic**
- Create `getSuggestions(courseName)` helper.
- Update `handleCourseChange` to generate and set suggestions.

**Step 2: UI for Suggestions**
- Render suggestion "pills" under the Search Query input.
- Click handler to set the query state.

**Step 3: Commit**
```bash
git add devorbit-web/src/components/admin/ScanForm.tsx
git commit -m "feat: add smart search query suggestions to ScanForm"
```

---

### Task 3: Final Polishing & Testing

**Files:**
- Test: Manually verify on the Admin UI

**Step 1: Verify Dropdown**
- Check if courses are loaded and selectable.

**Step 2: Verify Suggestions**
- Pick a course and check if pills appear.
- Click a pill and verify it fills the search box.

**Step 3: Run Scan**
- Perform a scan and ensure candidates are found.

**Step 4: Commit**
```bash
git commit -m "docs: finalize github scan optimization"
```
