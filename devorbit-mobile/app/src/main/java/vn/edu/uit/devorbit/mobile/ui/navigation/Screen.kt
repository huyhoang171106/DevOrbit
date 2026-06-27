package vn.edu.uit.devorbit.mobile.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Tổng quan", Icons.Rounded.Home)
    object Courses : Screen("courses", "Môn học", Icons.Rounded.List)
    object Knowledge : Screen("knowledge", "Kiến thức", Icons.Rounded.Star)
    object Explore : Screen("explore", "Khám phá", Icons.Rounded.Search)
    object Notifications : Screen("notifications", "Thông báo", Icons.Rounded.Notifications)
    object Profile : Screen("profile", "Cá nhân", Icons.Rounded.Person)
    object TaskManagement : Screen("tasks", "Nhiệm vụ", Icons.Rounded.CheckCircle)
    object GroupPlanList : Screen("group_plans", "Kế hoạch nhóm", Icons.Rounded.Group)
    data class GroupPlanDetail(val planId: Long) : Screen(
        "group_plan_detail/$planId", "", Icons.Rounded.CheckCircle
    )
    object Community : Screen("community", "Cộng đồng", Icons.Rounded.Chat)
}
