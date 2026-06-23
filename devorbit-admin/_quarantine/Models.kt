package vn.edu.uit.devorbit.admin.data

import androidx.compose.ui.graphics.Color
import vn.edu.uit.devorbit.admin.components.ResponseSectionType
import vn.edu.uit.devorbit.admin.components.SourceData
import vn.edu.uit.devorbit.admin.design.OrbitColors

// ═════════════════════════════════════════════════════════════════════════════
// Enums
// ═════════════════════════════════════════════════════════════════════════════

/** Overall subject status for the student dashboard. */
enum class SubjectStatus {
    Active,
    Paused,
    Completed,
    Locked,
}

/** Module node state within a subject's learning path.
 *  Mirrors [vn.edu.uit.devorbit.admin.components.PathNodeState] for UI binding. */
enum class ModuleState {
    Locked,
    Available,
    Current,
    Completed,
}

/** Tutor interaction modes. */
enum class TutorMode {
    Explain,
    Practice,
    Review,
    Debug,
}

/** Role of a message participant. */
enum class MessageRole {
    User,
    AI,
}

// ═════════════════════════════════════════════════════════════════════════════
// Subject & Learning
// ═════════════════════════════════════════════════════════════════════════════

data class Subject(
    val id: String,
    val code: String,
    val title: String,
    val description: String = "",
    val credits: Int = 0,
    val difficulty: String = "",
    val progress: Float = 0f,
    val status: SubjectStatus = SubjectStatus.Locked,
    val color: Color = OrbitColors.PrimaryElectricBlue,
    val nextTask: String = "",
    val estimatedTime: String = "",
    val semester: String = "",
    val isBookmarked: Boolean = false,
    val modules: List<SubjectModule> = emptyList(),
)

data class SubjectModule(
    val id: String,
    val title: String,
    val description: String = "",
    val state: ModuleState = ModuleState.Locked,
    val progress: Float = 0f,
    val resources: List<Resource> = emptyList(),
)

data class Resource(
    val id: String,
    val title: String,
    val type: String = "",          // "video", "article", "documentation", "quiz", etc.
    val url: String = "",
    val source: String = "",        // source name or author
    val relevance: Float = 0f,      // 0.0–1.0
)

// ═════════════════════════════════════════════════════════════════════════════
// Study Plan & Sessions
// ═════════════════════════════════════════════════════════════════════════════

data class StudySession(
    val id: String,
    val title: String,
    val subjectId: String,
    val date: String,               // ISO-8601 date string
    val startTime: String,          // "HH:mm"
    val duration: String,           // "45 min", "2h 30m"
    val completed: Boolean = false,
    val color: Color = OrbitColors.ChartBlue,
)

data class StudyPlan(
    val id: String,
    val title: String,
    val startDate: String,          // ISO-8601
    val endDate: String,            // ISO-8601
    val phases: List<PlanPhase> = emptyList(),
    val sessions: List<StudySession> = emptyList(),
)

data class PlanPhase(
    val id: String,
    val title: String,
    val description: String = "",
    val startDay: Int,              // day offset from plan start
    val endDay: Int,
    val modules: List<String> = emptyList(),   // module ids
    val completed: Boolean = false,
)

// ═════════════════════════════════════════════════════════════════════════════
// AI Tutor
// ═════════════════════════════════════════════════════════════════════════════

data class TutorMessage(
    val id: String,
    val role: MessageRole,
    val mode: TutorMode,
    val content: String = "",
    val sections: List<ResponseSection> = emptyList(),
    val sources: List<SourceData> = emptyList(),
)

data class ResponseSection(
    val type: ResponseSectionType,
    val title: String = "",
    val content: String = "",
)

// SourceData is reused from vn.edu.uit.devorbit.admin.components.SourceData

// ═════════════════════════════════════════════════════════════════════════════
// Profile & Achievements
// ═════════════════════════════════════════════════════════════════════════════

data class Achievement(
    val id: String,
    val title: String,
    val description: String = "",
    val icon: String = "",           // icon identifier / emoji name
    val unlocked: Boolean = false,
    val progress: Float = 0f,        // 0.0–1.0
    val unlockedAt: String? = null,  // ISO-8601
)

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val avatar: String = "",         // URL or placeholder
    val level: Int = 1,
    val xp: Long = 0,
    val streak: Int = 0,
    val totalHours: Float = 0f,
    val joinDate: String = "",       // ISO-8601
    val goals: List<String> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
)

// ═════════════════════════════════════════════════════════════════════════════
// Analytics & Dashboard
// ═════════════════════════════════════════════════════════════════════════════

data class SubjectDistribution(
    val subject: String,
    val hours: Float = 0f,
    val color: Color = OrbitColors.ChartBlue,
)

data class WeeklyStats(
    val weekStart: String,           // ISO-8601 date, typically Monday
    val focusHours: Float = 0f,
    val completionRate: Float = 0f,  // 0.0–1.0
    val sessionsCompleted: Int = 0,
)
