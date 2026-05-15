# RESEARCH.md — Phase 1: Foundation & Data Sync

## 1. Sync Architecture (Offline-First)

The "Academic OS" requires high responsiveness. We will implement the **Repository Pattern** with **Room** as the single source of truth.

### Data Flow:
`API` → `RemoteDataSource` → `Repository` → `RoomDatabase` → `LocalDataSource` → `ViewModel` → `UI`

### Pattern: Stale-While-Revalidate
1. UI displays data from Room immediately.
2. Repository triggers a background API fetch.
3. On success, Room is updated.
4. UI observes Room changes and updates reactively.

## 2. Room Schema Design

### Entity: `Course`
- `id` (Primary Key)
- `maMH` (Course Code)
- `tenMH` (Course Name)
- `credits`
- `description`
- `lastUpdated` (Timestamp for sync)

### Entity: `Repository`
- `id` (Primary Key)
- `courseId` (Foreign Key)
- `displayName`
- `description`
- `githubUrl`
- `primaryLanguage`
- `stars`
- `aiClassification` (Enum: PROJECT, LECTURE, NOTES, etc.)

### Entity: `CourseRelationship` (Prerequisites)
- `id`
- `fromCourseId`
- `toCourseId`
- `type` (PREREQUISITE, COREQUISITE)

## 3. Pillar Scaffolding (Navigation)

### 5-Pillar Bottom Bar
Using `NavigationSuiteScaffold` or standard `NavigationBar` in Compose.
1. **Dashboard**: `DashboardScreen`
2. **Courses**: `CourseHubScreen`
3. **Progress**: `ProgressTrackerScreen` (PTB)
4. **Execution**: `TodoScreen`
5. **AI Copilot**: `CopilotScreen`

## 4. Dashboard (Mission Control) Strategy

The Dashboard will be a `LazyColumn` of specialized widgets:
- `DailyBriefingCard`: Summary of today's academic state.
- `DeadlineClusterWidget`: Horizontal list of upcoming tasks.
- `SemesterRiskGauge`: Visual indicator of academic risk.
- `WorkloadHeatmap`: 7-day grid showing task density.

## 5. API Integration Points

| Feature | Backend Endpoint | Method |
|---------|------------------|--------|
| Course List | `/api/courses` | GET |
| Repo Discovery | `/api/courses/{id}/repos` | GET |
| Dependency Graph | `/api/courses/relationships` | GET |
| Recent Activity | `/api/discovery/recent-repos` | GET |
| AI Summary | `/api/ai/repo/{id}/summary` | GET |

## 6. Implementation Notes
- Use **Retrofit** for API calls.
- Use **Hilt** for Dependency Injection.
- Use **Kotlin Flows** for reactive data updates from Room.
- **Image Loading**: Use **Coil** for repo avatars/icons.
