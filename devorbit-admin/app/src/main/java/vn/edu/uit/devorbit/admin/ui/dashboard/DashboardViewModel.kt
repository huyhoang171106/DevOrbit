package vn.edu.uit.devorbit.admin.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.core.designsystem.StatusType
import vn.edu.uit.devorbit.admin.data.remote.dto.AdminStatsResponse
import vn.edu.uit.devorbit.admin.data.remote.dto.RepoCandidateResponse
import vn.edu.uit.devorbit.admin.data.remote.dto.RepoReviewAdminResponse
import vn.edu.uit.devorbit.admin.data.remote.dto.CommunityMessageAdminResponse
import vn.edu.uit.devorbit.admin.data.remote.dto.AdminStudentResponse
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

// ── Status ─────────────────────────────────────────────────────────────────────

sealed class DashboardStatus {
    data object InitialLoading : DashboardStatus()
    data class Loading(val cachedData: DashboardData? = null) : DashboardStatus()
    data class Error(val message: String) : DashboardStatus()
    data class Success(val data: DashboardData) : DashboardStatus()
}

// ── UI State ───────────────────────────────────────────────────────────────────

data class DashboardUiState(
    val status: DashboardStatus = DashboardStatus.InitialLoading,
    val unreadCount: Int = 0,
    val adminName: String = "Quản trị viên",
    val lastSyncTime: String? = null,
    val apiStatus: ApiStatus = ApiStatus.Connected,
)

enum class ApiStatus { Connected, Disconnected, Syncing }

// ── Domain data ────────────────────────────────────────────────────────────────

data class DashboardData(
    val totalStudents: Long = 0,
    val totalCourses: Long = 0,
    val totalRepos: Long = 0,
    val pendingCandidatesCount: Long = 0,
    val priorityItems: List<PriorityItemData> = emptyList(),
    val recentActivities: List<ActivityItemData> = emptyList(),
)

data class PriorityItemData(
    val type: String,
    val label: String,
    val time: String,
    val entity: String,
    val severity: StatusType = StatusType.INFO,
)

data class ActivityItemData(
    val title: String,
    val subtitle: String,
    val time: String,
)

// ── ViewModel ──────────────────────────────────────────────────────────────────

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init { loadDashboard() }

    fun loadDashboard() {
        if (_state.value.status is DashboardStatus.Success) {
            _state.value = _state.value.copy(
                status = DashboardStatus.Loading(
                    cachedData = (_state.value.status as DashboardStatus.Success).data,
                ),
            )
        } else {
            _state.value = _state.value.copy(status = DashboardStatus.InitialLoading)
        }

        viewModelScope.launch {
            // Fetch aggregated stats as the primary source
            val statsDeferred = async { adminRepository.getStats() }

            // Fetch supplementary data in parallel
            val candidatesDeferred = async { adminRepository.getPendingCandidates("all") }
            val reviewsDeferred = async { adminRepository.getRepoReviews() }
            val communityDeferred = async { adminRepository.getCommunityMessages() }
            val scanLogsDeferred = async { adminRepository.getScanLogs() }
            val studentsDeferred = async { adminRepository.getStudents() }
            val unreadDeferred = async { adminRepository.getUnreadCount() }

            // Handle stats (primary data — error here means showing error state)
            val statsResult = statsDeferred.await()
            val dashboardData = statsResult.fold(
                onSuccess = { stats -> buildDashboardData(stats) },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        status = DashboardStatus.Error(
                            e.message ?: "Không thể tải dữ liệu thống kê",
                        ),
                    )
                    return@launch
                },
            )

            // Merge supplementary data into the dashboard
            val candidates = candidatesDeferred.await().getOrDefault(emptyList())
            val reviews = reviewsDeferred.await().getOrDefault(emptyList())
            val messages = communityDeferred.await().getOrDefault(emptyList())
            val scanLogs = scanLogsDeferred.await().getOrDefault(emptyList())
            val students = studentsDeferred.await().getOrDefault(emptyList())

            val priorityItems = buildPriorityItems(
                candidates = candidates,
                reviews = reviews,
                messages = messages,
                scanLogs = scanLogs,
                students = students,
            )

            val fullData = dashboardData.copy(priorityItems = priorityItems)

            val unreadCount = unreadDeferred.await().getOrDefault(0L).toInt()

            _state.value = _state.value.copy(
                status = DashboardStatus.Success(fullData),
                unreadCount = unreadCount,
                lastSyncTime = currentTimeString(),
            )
        }
    }

    fun refresh() {
        _state.value = _state.value.copy(apiStatus = ApiStatus.Syncing)
        loadDashboard()
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private fun buildDashboardData(stats: AdminStatsResponse): DashboardData {
        val activities = buildActivities(stats)
        return DashboardData(
            totalStudents = stats.totalStudents,
            totalCourses = stats.totalCourses,
            totalRepos = stats.totalRepos,
            pendingCandidatesCount = stats.pendingCandidates,
            recentActivities = activities,
        )
    }

    private fun buildActivities(stats: AdminStatsResponse): List<ActivityItemData> {
        val items = mutableListOf<ActivityItemData>()

        stats.recentStudents.take(5).forEach { s ->
            items.add(
                ActivityItemData(
                    title = s.fullName,
                    subtitle = "Đã tham gia hệ thống",
                    time = "Vừa xong",
                ),
            )
        }

        stats.recentCourseReviews.take(5).forEach { r ->
            items.add(
                ActivityItemData(
                    title = "${r.studentName} đánh giá ${r.courseName}",
                    subtitle = "Xếp hạng: ${r.rating}/5",
                    time = r.createdAt ?: "",
                ),
            )
        }

        stats.recentSubmissions.take(5).forEach { s ->
            items.add(
                ActivityItemData(
                    title = "Bài nộp ${s.courseName}",
                    subtitle = s.githubUrl,
                    time = s.status,
                ),
            )
        }

        return items.sortedByDescending { it.time }.take(10)
    }

    private fun buildPriorityItems(
        candidates: List<RepoCandidateResponse>,
        reviews: List<RepoReviewAdminResponse>,
        messages: List<CommunityMessageAdminResponse>,
        scanLogs: List<String>,
        students: List<AdminStudentResponse>,
    ): List<PriorityItemData> {
        val items = mutableListOf<PriorityItemData>()

        // 1. Pending candidates
        candidates.forEach { c ->
            items.add(
                PriorityItemData(
                    type = "Ứng viên kho",
                    label = c.githubName ?: c.githubUrl ?: "Không tên",
                    time = c.lastPushedAt?.let { formatTime(it) } ?: "Chờ xử lý",
                    entity = c.courseName ?: c.courseCode ?: "Chưa phân loại",
                    severity = StatusType.WARNING,
                ),
            )
        }

        // 2. Reviews needing attention (low ratings)
        reviews.filter { it.rating != null && it.rating <= 2 }.forEach { r ->
            items.add(
                PriorityItemData(
                    type = "Đánh giá thấp",
                    label = "${r.studentName} - ${r.repoName}",
                    time = r.createdAt?.let { formatTime(it) } ?: "",
                    entity = "Xếp hạng: ${r.rating}/5",
                    severity = StatusType.DANGER,
                ),
            )
        }

        // 3. Community messages needing attention
        messages.take(5).forEach { m ->
            items.add(
                PriorityItemData(
                    type = "Cộng đồng",
                    label = m.content.take(60),
                    time = m.createdAt?.let { formatTime(it) } ?: "",
                    entity = "${m.studentName} trong ${m.channelName ?: "kênh chung"}",
                    severity = StatusType.INFO,
                ),
            )
        }

        // 4. Scan errors
        scanLogs.filter { it.contains("error", ignoreCase = true) || it.contains("lỗi", ignoreCase = true) }
            .take(3)
            .forEach { log ->
                items.add(
                    PriorityItemData(
                        type = "Lỗi quét",
                        label = log.take(80),
                        time = "Gần đây",
                        entity = "Scan logs",
                        severity = StatusType.DANGER,
                    ),
                )
            }

        // 5. Unverified students
        students.filter { !it.emailVerified || !it.active }.take(5).forEach { s ->
            items.add(
                PriorityItemData(
                    type = "Sinh viên chưa xác thực",
                    label = s.fullName,
                    time = s.studentCode,
                    entity = if (!s.emailVerified) "Chưa xác thực email" else "Tài khoản bị vô hiệu",
                    severity = StatusType.WARNING,
                ),
            )
        }

        return items.take(15)
    }

    private fun formatTime(iso: String): String {
        // Simple fallback: just return a short representation
        return if (iso.length >= 10) iso.substring(0, 10) else iso
    }

    private fun currentTimeString(): String {
        val cal = java.util.Calendar.getInstance()
        val h = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val m = cal.get(java.util.Calendar.MINUTE)
        return "%02d:%02d".format(h, m)
    }
}
