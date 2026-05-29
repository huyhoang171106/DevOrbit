# Nghiên cứu Cải thiện 4 Màn hình — Giá trị & Pain Points

> DevOrbit Mobile — Platform học thuật UIT
> 2026-05-28 | Based on codebase analysis + live API testing + UX critique

---

## Tóm tắt Tổng quan

4 screen này đang **functional nhưng chưa addictive**.

Đang giống **dashboard CRUD API** hơn là **product có UX mạnh**.

Vấn đề lớn nhất không phải "thiếu feature", mà là chưa có **core loop** — lý do để user quay lại mỗi ngày.

---

## 0. Thiếu lớn nhất: App chưa có "Core Loop"

### Hiện tại

```
Open app → browse → close app
```

### App mạnh cần

```
Open → discover → plan → execute → track progress → return tomorrow
```

### Thiếu

- Retention loop
- Habit loop
- Emotional reward
- "1 reason people must use this"

### Hook nên có

> **"AI-powered CS learning graph for UIT students"**

Tất cả xoay quanh **learning progression**:

1. Prerequisite graph — "môn nào cần học trước"
2. Repo graph — "tài liệu nào cho môn này"
3. Personalized roadmap — "bạn nên học gì tiếp"
4. AI mentor — "hỏi gì cũng được"
5. Execution planner — "hôm nay học gì"

Nếu focus đúng cái này → app có identity.
Nếu tiếp tục thêm random feature → "another student utility app" → chết UX.

---

## 1. Courses Screen — đang functional nhưng chưa addictive

### Vấn đề

Flow hiện tại:

```
mở app → list môn → click → xem repos
```

Nó "đúng", nhưng không tạo cảm giác:
- Tiến bộ
- Khám phá
- Personalization
- Motivation

**Giống admin panel hơn là learning app.**

### A. Learning Progress — CHƯA CÓ

Hiện không có:
- Đã học gì
- Đang học gì
- Completion %
- Streak
- Estimated difficulty

User không có cảm giác "đang build skill tree".

**Nên thêm:**
- Progress ring cho mỗi môn
- "Bạn đã hoàn thành 3/10 repos"
- Estimated mastery
- Completion badge

### B. Course Graph — ĐANG PHÍ

API graph có rồi (30 nodes, 18 links) mà đang phí.

Nếu chỉ render list → mất hết "wow factor".

**Nên:**
- Interactive prerequisite graph
- Node unlock animation
- Roadmap visualization

```
DSA → OOP → Backend → Distributed Systems
```

Cái này mới tạo identity cho app.

### C. Repo Quality Signal — CHƯA CÓ

Hiện repo chỉ là list. Thiếu:
- Repo difficulty
- Production-grade?
- Beginner-friendly?
- Outdated?
- Archived?
- Stars/forks
- "Recommended by AI"

Không có → user drown trong repo noise.

### Summary

| Feature | Hiện tại | Nên có |
|---------|----------|--------|
| Search/filter | ❌ Không có | ✅ Gõ 3 ký tự → filter real-time |
| Learning progress | ❌ Không có | ✅ Progress ring per course |
| Course graph | ❌ Render list | ✅ Interactive prerequisite graph |
| Repo quality | ❌ Raw list | ✅ AI-rated + difficulty |
| Bookmark | ❌ Mock in-memory | ✅ API persist |
| Difficulty indicator | ❌ Không có | ✅ Compute từ credits + repoCount |

---

## 2. Explore Screen — yếu nhất trong 4 screen

Hiện gần như:

> recent repos + tech stacks

**Chưa có lý do để user quay lại mỗi ngày.**

### A. Discovery Feed — CHƯA CÓ

Explore nên giống:
- GitHub Explore
- Product Hunt
- Reddit for students

Chứ không phải static list.

**Nên có:**
- Trending this week
- Popular among UIT students
- AI picks
- Repos similar to what you bookmarked

### B. Social Proof — CHƯA CÓ

Zero social signal hiện tại.

**Nên có:**
- "120 students saved this"
- "Used in SE346"
- "Recommended for backend path"

Social proof cực mạnh cho learning apps.

### C. Personalized Recommendation — CHƯA CÓ

Chưa personalized.

Backend AI đã có roadmap/query, nhưng Explore chưa tận dụng.

**Nên có:**
- "Because you studied Java"
- "Next repo after OOP"
- "Recommended for SWE path"

### Summary

| Feature | Hiện tại | Nên có |
|---------|----------|--------|
| Recent repos | ✅ 10 repos | ✅ + Trending + AI picks |
| Tech stacks | ❌ Rỗng | ✅ + Filter by popularity |
| Repo detail nav | ❌ Không có | ✅ Click → AI summary |
| Social proof | ❌ Không có | ✅ "X students saved" |
| Personalization | ❌ Không có | ✅ "Because you studied..." |
| Language filter | ❌ Không có | ✅ Filter chips |
| Search | ❌ Không có | ✅ Search repos |

---

## 3. Plan Screen — tiềm năng lớn nhất nhưng hiện chưa đủ mạnh

Đây là phần có thể biến app từ "resource browser" thành **"daily tool"**.

Nhưng hiện đang hơi **generic todo app**.

### A. AI Roadmap chưa grounded — PROBLEM LỚN

AI roadmap dễ thành:

> học Java → Spring → Docker → AWS

**Generic AI slop.**

Cần:
- Grounded vào actual UIT courses
- Actual repos
- Actual prerequisites

Ví dụ:

```
Bạn đang học:
- OOP
- DSA

=> Recommend:
- repo X (C++ DSA implementations)
- course Y (IT003 Cấu trúc dữ liệu)
- mini project Z (Build a library management system)
```

Nếu không → roadmap sẽ vô dụng sau 2 phút.

### B. Thiếu Execution Loop

Plan chỉ generate rồi nằm đó.

Thiếu:
- Daily tasks
- Reminders
- Weekly review
- "You are behind schedule"
- Adaptive replanning

### C. Không có Dopamine System

Learning apps chết vì thiếu feedback loop.

**Nên có:**
- Streak
- XP
- Milestones
- "Bạn đã hoàn thành Backend Foundation"

Không cần gamify cringe. Chỉ cần **visible progress**.

### Summary

| Feature | Hiện tại | Nên có |
|---------|----------|--------|
| AI roadmap | ❌ Không connect | ✅ Input → grounded courses |
| Study plan persistence | ❌ Mất khi restart | ✅ DataStore persist |
| Daily tasks | ❌ Không có | ✅ "Hôm nay học gì" |
| Progress tracking | ❌ Toggle rỗng | ✅ Completion + streak |
| Workload balance | ✅ Engine có | ✅ Connected to real data |
| Deadline awareness | ❌ Không có | ✅ "Còn 2 tuần thi" |
| Milestones | ❌ Không có | ✅ "Backend Foundation done" |

---

## 4. Profile Screen — currently too empty

Hiện giống **settings page** hơn là **profile**.

### A. Learning Identity — CHƯA CÓ

**Nên có:**
- Current path
- Strongest stacks
- Learning heatmap
- Favorite domains

Ví dụ:

```
Backend Engineer Path
Top stack: Spring Boot
Current focus: Distributed Systems
```

### B. Portfolio Integration — CHƯA CÓ

Vì app xoay quanh repos, nên:
- Connect GitHub
- Show contributions
- Save own projects
- Build portfolio graph

Cái này cực hợp DevOrbit direction.

### Summary

| Feature | Hiện tại | Nên có |
|---------|----------|--------|
| Student info | ✅ Name + code | ✅ + Avatar + path |
| Bookmarks | ❌ Mock in-memory | ✅ API persist + click nav |
| Dark mode | ✅ Toggle | ✅ |
| Learning stats | ❌ Không có | ✅ "12 repos tuần này" |
| Current path | ❌ Không có | ✅ "Backend Engineer" |
| Portfolio | ❌ Không có | ✅ GitHub integration |

---

## 5. UI/UX Direction

### Đừng làm

- Nhiều card
- Nhiều CRUD section
- Enterprise dashboard

→ Sẽ chết rất nhanh.

### Nên đi

- **Developer tool aesthetic**
- Graph-centric
- Minimal dark mode
- "Knowledge operating system"

### Reference apps

| App | Takeaway |
|-----|----------|
| **Linear** | Clean, minimal, keyboard-first |
| **GitHub** | Graph + contribution + social proof |
| **Raycast** | Speed + command palette + personalization |
| **Obsidian** | Knowledge graph + backlinks + daily notes |
| **Read.cv** | Identity + portfolio + minimalist |

---

## 6. Core Loop Design

### Daily Loop

```
Morning:
  → "Hôm nay bạn cần học 2 môn"
  → Daily task 1: "Đọc repo DSA-W4-18052023"
  → Daily task 2: "Hoàn thành lab OOP"

Evening:
  → Toggle completed
  → Streak +1
  → "Bạn đang đi đúng hướng"
```

### Weekly Loop

```
Monday:
  → Weekly review: "Tuần trước hoàn thành 8/10 tasks"
  → Updated roadmap: "Tuần này tập trung Backend"

Friday:
  → Milestone check: "Bạn đã hoàn thành Backend Foundation"
  → Recommendation: "Repo tiếp theo: Spring Boot CRUD"
```

### Retention Hooks

1. **Streak** — "7 ngày liên tiếp học tập"
2. **Progress visibility** — "65% hoàn thành HK1"
3. **Social proof** — "120 sinh viên đang học cùng"
4. **Personalization** — "Dựa trên OOP của bạn..."
5. **FOMO nhẹ** — "3 repos mới tuần này"

---

## 7. Ưu tiên Implement

### Phase 1: Core Loop (3-4 ngày)

| # | Feature | Screen | Effort | Impact |
|---|---------|--------|--------|--------|
| 1 | **Connect Plan to AI roadmap** | Plan | Medium | 🔥🔥🔥 |
| 2 | **Search + Filter courses** | Courses | Low | 🔥🔥🔥 |
| 3 | **API bookmarks** (thay mock) | Profile | Medium | 🔥🔥 |
| 4 | **Repo detail nav từ Explore** | Explore | Low | 🔥🔥 |
| 5 | **Persist study plan** | Plan | Medium | 🔥🔥 |

### Phase 2: Learning Progress (3-4 ngày)

| # | Feature | Screen | Effort | Impact |
|---|---------|--------|--------|--------|
| 6 | **Progress ring per course** | Courses | Medium | 🔥🔥🔥 |
| 7 | **Daily tasks view** | Plan | Medium | 🔥🔥🔥 |
| 8 | **Learning stats** | Profile | Low | 🔥🔥 |
| 9 | **Bookmark từ CourseDetail** | Courses | Low | 🔥🔥 |
| 10 | **Completion streak** | Plan | Low | 🔥🔥 |

### Phase 3: Discovery & Identity (3-4 ngày)

| # | Feature | Screen | Effort | Impact |
|---|---------|--------|--------|--------|
| 11 | **Interactive course graph** | Courses | High | 🔥🔥🔥 |
| 12 | **Discovery feed** | Explore | Medium | 🔥🔥 |
| 13 | **Learning identity** | Profile | Medium | 🔥🔥 |
| 14 | **Social proof** | Explore | Medium | 🔥🔥 |
| 15 | **Milestones** | Plan | Low | 🔥🔥 |

### Phase 4: Polish (2-3 ngày)

| # | Feature | Screen | Effort | Impact |
|---|---------|--------|--------|--------|
| 16 | **AI summary preview** | Explore | Medium | 🔥 |
| 17 | **Portfolio integration** | Profile | High | 🔥 |
| 18 | **Weekly review** | Plan | Medium | 🔥 |
| 19 | **Language/course filter** | Explore | Low | 🔥 |
| 20 | **Prerequisite chain** | Courses | Low | 🔥 |

---

## 8. Success Metrics

| Metric | Hiện tại | Mục tiêu |
|--------|----------|----------|
| Daily active use | Không có | 3x/tuần |
| Courses search | Không có | Gõ 3 ký tự → filter |
| Plan generation | Rỗng | Nhập mục tiêu → plan trong 3s |
| Bookmark persist | Mất khi restart | Lưu server, sync |
| Repo navigation | Không có | Click → detail trong 1 tap |
| Progress visibility | Không có | Completion % per course |
| Streak | Không có | Daily streak counter |

---

## 9. Technical Constraints

### Backend Issues cần fix trước

| Issue | Impact | Fix |
|-------|--------|-----|
| **Login bug** | Không test auth | Fix password hash |
| **tech_stacks rỗng** | Explore rỗng | Populate table |
| **techStack filter sai** | Filter không hoạt động | Fix query |
| **Subject Q&A 500** | AI chatbot chết | Implement endpoint |
| **impactScore=0** | Graph vô nghĩa | Tính impact |

### Mobile Changes cần làm

| File | Changes |
|------|---------|
| `.env` | Update API_BASE_URL → cloud |
| `ApiService.kt` | Bookmark endpoints + fix types |
| `BookmarkRepositoryImpl.kt` | Mock → API |
| `CourseHubScreen.kt` | Search + filter + progress |
| `ExploreScreen.kt` | Search + nav + social proof |
| `StudyPlannerScreen.kt` | Input dialog + daily tasks |
| `ProfileScreen.kt` | Stats + bookmark nav + identity |
| `MainScreen.kt` | Connect PlanTabView |

---

## 10. 总结

### Giá trị cốt lõi DevOrbit nên mang

1. **Courses** → Giúp sinh viên **chọn môn đúng** + **thấy tiến bộ**
2. **Explore** → Giúp sinh viên **khám phá có mục đích** + **social proof**
3. **Plan** → Biến app thành **daily tool** + **visible progress**
4. **Profile** → **Learning identity** + **portfolio**

### Hook chính

> **"AI-powered CS learning graph for UIT students"**

Tất cả xoay quanh: **learning progression**

### Không cần làm

- ❌ Login/Register (deferred)
- ❌ AI photobooth (không liên quan)
- ❌ Admin features
- ❌ Random gamification
- ❌ Enterprise dashboard UI
