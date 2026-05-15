# DevOrbit Mobile — Product Spec v2

> **Last Updated:** 2026-05-16
> **Status:** Phases 0-4 implemented; Plan tab features pending

## 1. Vấn đề & Cơ hội

### Hiện trạng (sau refactor)

DevOrbit Mobile là Android app (Kotlin + Jetpack Compose) đã được refactor hoàn toàn sang Clean Architecture:

- ✅ **Clean Architecture**: `domain/`, `data/`, `ui/` layers tách biệt
- ✅ **DI thống nhất**: Chỉ dùng Hilt (xoá NetworkModule object)
- ✅ **Navigation duy nhất**: `Screen` sealed class (6 tabs)
- ✅ **Không còn class trùng tên**
- ✅ **9 domain engines** với 106 unit tests
- ✅ **Auth flow**: Login/Register OTP + JWT + DataStore
- ✅ **6 tabs real**: Dashboard, Courses, Knowledge, Explore, Plan (stub), Profile

## 2. Vision: "Người bạn đồng hành học tập cho sinh viên UIT" (MAINTAINED ✅)

DevOrbit Mobile là app giúp sinh viên UIT:
- Khám phá môn học, kho mã nguồn, tech stack ✅
- Hiểu chương trình đào tạo qua Knowledge Graph ✅
- Học tập với tài nguyên số (YouTube, articles, tutorials) ✅
- Tổ chức việc học với planner, task breakdown ✅ (pending wiring)
- Theo dõi tiến độ, workload, GPA ⏳ (GPA screen pending)
- Tương tác với AI để hỏi về môn học và repo ✅

## 3. Navigation & Màn hình (Implementation Status)

### 6 tabs navigation (✅ IMPLEMENTED)

| # | Tab | Icon | Status |
|---|---|---|---|
| 🏠 | Dashboard | Home | ✅ Implemented |
| 📚 | Courses | Book | ✅ Implemented |
| 🧠 | Knowledge | Psychology | ✅ Implemented |
| 🔭 | Explore | Compass | ✅ Implemented |
| 📋 | Plan | Assignment | 🔴 Stub — pending PlanTabHost, PlanViewModel |
| 👤 | Profile | Person | ✅ Implemented (with Auth) |

### Feature Status

| Feature | Status |
|---------|--------|
| Course List + Detail | ✅ Refactored |
| Repo Detail + Tech Stack | ✅ Refactored |
| Course Relationships | ✅ Refactored |
| Knowledge Graph | ✅ Refactored |
| Resources (tutorials/videos/articles) | ✅ UI exists |
| AI Repo Summary | ✅ API + UI |
| AI Tutor Advice | ✅ API + UI |
| AI KG Query | ✅ API; UI pending |
| Tech Stack List | ✅ |
| Discovery Feed | ✅ |
| Student Auth (OTP) | ✅ |
| Study Planner | ⏳ Pending wiring |
| Task Breakdown | ⏳ Pending wiring |
| GPA Forecast | ⏳ Pending |
| Flashcards | ⏳ Pending |
| Subject Notes | ⏳ Pending |
| What-If GPA | ⏳ Pending |
| Study Timer | ⏳ Pending |
| Deadline Calendar | ⏳ Pending |
| Study Streaks/XP | ⏳ Pending |
| Contribution Heatmap | ⏳ Pending |
| Course Comparison | ⏳ Pending |

## 4. Behavior Invariants (MAINTAINED ✅)

1. Offline-first ✅
2. Không crash khi API fail ✅
3. JWT persistent ✅
4. Graph động ✅
5. Engine stateless ✅
6. Navigation đồng nhất ✅
7. DI thống nhất ✅
8. Không class trùng tên ✅
9. Test coverage > 60% cho engines ✅ (106 tests)

## 5. Non-goals (MAINTAINED ✅)

- AI Roadmap Generator (backlog) — deferred per user request
- Learning Roadmap Viewer (backlog)
- Course Comparison (backlog)
- Grade Tracker / What-If GPA — engines ready, UI pending
- All other items in this list remain deferred
