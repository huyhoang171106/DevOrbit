package vn.edu.uit.devorbit.admin.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

object AdminRoutes {
    const val DASHBOARD = "dashboard"
    const val STUDENTS = "students"
    const val COURSES = "courses"
    const val COURSE_DETAIL = "courses/{courseId}"
    const val COURSE_RELATIONSHIPS = "course-relationships"
    const val REPOS = "repos"
    const val CANDIDATES = "candidates"
    const val REVIEWS = "reviews"
    const val GITHUB = "github"
    const val COMMUNITY = "community"
    const val TECHSTACK = "techstack"
    const val NOTES = "notes"
    const val NOTIFICATIONS = "notifications"
    const val REPORTS = "reports"

    fun courseDetail(courseId: Long) = "courses/$courseId"
}

/** 4 primary tabs for bottom navigation */
sealed class PrimaryTab(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : PrimaryTab(AdminRoutes.DASHBOARD, "Tổng quan", Icons.Rounded.Dashboard)
    object Students : PrimaryTab(AdminRoutes.STUDENTS, "Sinh viên", Icons.Rounded.People)
    object Courses : PrimaryTab(AdminRoutes.COURSES, "Môn học", Icons.Rounded.MenuBook)
    object Repos : PrimaryTab(AdminRoutes.REPOS, "Kho", Icons.Rounded.Folder)

    companion object {
        val items = listOf(Dashboard, Students, Courses, Repos)
    }
}

/** All screens available in the command hub */
sealed class AdminScreen(val route: String, val label: String, val icon: ImageVector, val category: String) {
    // Primary (bottom nav)
    object Dashboard : AdminScreen(AdminRoutes.DASHBOARD, "Tổng quan", Icons.Rounded.Dashboard, "Chính")
    object Students : AdminScreen(AdminRoutes.STUDENTS, "Sinh viên", Icons.Rounded.People, "Chính")
    object Courses : AdminScreen(AdminRoutes.COURSES, "Môn học", Icons.Rounded.MenuBook, "Chính")
    object Repos : AdminScreen(AdminRoutes.REPOS, "Kho", Icons.Rounded.Folder, "Chính")

    // Secondary (command hub)
    object Candidates : AdminScreen(AdminRoutes.CANDIDATES, "Duyệt kho", Icons.Rounded.RateReview, "Quản lý")
    object Reviews : AdminScreen(AdminRoutes.REVIEWS, "Đánh giá", Icons.Rounded.Star, "Quản lý")
    object Github : AdminScreen(AdminRoutes.GITHUB, "Quét GitHub", Icons.Rounded.Code, "Công cụ")
    object Community : AdminScreen(AdminRoutes.COMMUNITY, "Cộng đồng", Icons.Rounded.Forum, "Quản lý")
    object TechStack : AdminScreen(AdminRoutes.TECHSTACK, "Công nghệ", Icons.Rounded.Settings, "Công cụ")
    object Reports : AdminScreen(AdminRoutes.REPORTS, "Báo cáo", Icons.Rounded.BarChart, "Quản lý")
    object Notes : AdminScreen(AdminRoutes.NOTES, "Ghi chú", Icons.Rounded.StickyNote2, "Quản lý")
    object Notifications : AdminScreen(AdminRoutes.NOTIFICATIONS, "Thông báo", Icons.Rounded.Notifications, "Hệ thống")

    companion object {
        val primaryTabs = listOf(Dashboard, Students, Courses, Repos)
        val secondaryScreens = listOf(Candidates, Reviews, Github, Community, TechStack, Reports, Notes, Notifications)
    }
}
