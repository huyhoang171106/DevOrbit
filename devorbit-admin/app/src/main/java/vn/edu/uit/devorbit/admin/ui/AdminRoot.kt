package vn.edu.uit.devorbit.admin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.core.designsystem.AdminScaffold
import vn.edu.uit.devorbit.admin.core.designsystem.NavGroup
import vn.edu.uit.devorbit.admin.ui.candidates.CandidatesScreen
import vn.edu.uit.devorbit.admin.ui.community.CommunityScreen
import vn.edu.uit.devorbit.admin.ui.courses.CoursesScreen
import vn.edu.uit.devorbit.admin.ui.dashboard.DashboardScreen
import vn.edu.uit.devorbit.admin.ui.github.GithubScreen
import vn.edu.uit.devorbit.admin.ui.login.LoginScreen
import vn.edu.uit.devorbit.admin.ui.login.LoginViewModel
import vn.edu.uit.devorbit.admin.ui.repos.ReposScreen
import vn.edu.uit.devorbit.admin.ui.reports.ReportsScreen
import vn.edu.uit.devorbit.admin.ui.reviews.ReviewsScreen
import vn.edu.uit.devorbit.admin.ui.students.StudentsScreen
import vn.edu.uit.devorbit.admin.ui.techstack.TechStackScreen
import vn.edu.uit.devorbit.admin.ui.theme.*

/** Top-level composable wired into MainActivity. */
@Composable
fun AdminRoot() {
    val loginVm: LoginViewModel = hiltViewModel()
    val loginState by loginVm.state.collectAsStateWithLifecycle()

    if (!loginState.sessionChecked) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = UITBlue)
        }
        return
    }

    if (loginState.isLoggedIn) {
        AdminHost(onLogout = { loginVm.logout() })
    } else {
        LoginScreen(
            viewModel = loginVm,
            onLoggedIn = { /* state drives recomposition */ }
        )
    }
}

/** Host shell with 5-group bottom navigation / rail */
@Composable
private fun AdminHost(onLogout: () -> Unit) {
    var selectedGroup by remember { mutableStateOf<NavGroup>(NavGroup.Command) }
    var contentTab by remember { mutableStateOf(0) }
    var moderationTab by remember { mutableStateOf(0) }

    AdminScaffold(
        selectedGroup = selectedGroup,
        onGroupSelected = { selectedGroup = it },
        topBarTitle = when (selectedGroup) {
            is NavGroup.Command -> "Điều hành"
            is NavGroup.Content -> "Nội dung"
            is NavGroup.Moderation -> "Kiểm duyệt"
            is NavGroup.Users -> "Người dùng"
            is NavGroup.More -> "Thêm"
        },
        onLogout = onLogout,
        showBottomBar = true,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedGroup) {
                is NavGroup.Command -> DashboardScreen()
                is NavGroup.Content -> ContentNavHost(
                    tab = contentTab,
                    onTabChange = { contentTab = it },
                )
                is NavGroup.Moderation -> ModerationNavHost(
                    tab = moderationTab,
                    onTabChange = { moderationTab = it },
                )
                is NavGroup.Users -> UsersNavHost()
                is NavGroup.More -> MoreNavHost()
            }
        }
    }
}

// ── Content (Nội dung) ────────────────────────────────────────────────────────
@Composable
private fun ContentNavHost(
    tab: Int,
    onTabChange: (Int) -> Unit,
) {
    val tabs = listOf("Môn học", "Kho", "Công nghệ", "Quét GitHub")
    Column {
        TabRow(
            selectedTabIndex = tab,
            containerColor = Surface,
            contentColor = UITBlue,
            divider = { Divider(color = Divider, thickness = 0.5.dp) },
        ) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = tab == index,
                    onClick = { onTabChange(index) },
                    text = {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (tab == index) UITBlue else TextSecondary,
                        )
                    },
                )
            }
        }
        when (tab) {
            0 -> CoursesScreen(onCourseClick = {}, onRelationshipsClick = {})
            1 -> ReposScreen()
            2 -> TechStackScreen()
            3 -> GithubScreen()
        }
    }
}

// ── Moderation (Kiểm duyệt) ───────────────────────────────────────────────────
@Composable
private fun ModerationNavHost(
    tab: Int,
    onTabChange: (Int) -> Unit,
) {
    val tabs = listOf("Ứng viên", "Đánh giá", "Cộng đồng", "AI Chat")
    Column {
        TabRow(
            selectedTabIndex = tab,
            containerColor = Surface,
            contentColor = UITBlue,
            divider = { Divider(color = Divider, thickness = 0.5.dp) },
        ) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = tab == index,
                    onClick = { onTabChange(index) },
                    text = {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (tab == index) UITBlue else TextSecondary,
                        )
                    },
                )
            }
        }
        when (tab) {
            0 -> CandidatesScreen()
            1 -> ReviewsScreen()
            2 -> CommunityScreen()
            3 -> CommunityScreen() // TODO: AI Chat Monitor
        }
    }
}

// ── Users (Người dùng) ────────────────────────────────────────────────────────
@Composable
private fun UsersNavHost() {
    StudentsScreen()
}

// ── More (Thêm) ───────────────────────────────────────────────────────────────
@Composable
private fun MoreNavHost() {
    ReportsScreen()
}
