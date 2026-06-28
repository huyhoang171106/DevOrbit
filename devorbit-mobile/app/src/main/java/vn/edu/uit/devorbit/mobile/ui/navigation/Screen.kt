package vn.edu.uit.devorbit.mobile.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Hôm nay", Icons.Rounded.Home)
    object Subjects : Screen("subjects", "Môn học", Icons.Rounded.Book)
    object Tutor : Screen("tutor", "AI Tutor", Icons.Rounded.AutoAwesome)
    object Plan : Screen("plan", "Kế hoạch", Icons.Rounded.DateRange)
    object Community : Screen("community", "Cộng đồng", Icons.Rounded.Chat)

    object Knowledge : Screen("knowledge", "Kiến thức", Icons.Rounded.Star)
    object Explore : Screen("explore", "Khám phá", Icons.Rounded.Search)
    object Notifications : Screen("notifications", "Thông báo", Icons.Rounded.Notifications)
    object Profile : Screen("profile", "Cá nhân", Icons.Rounded.Person)
    object TaskManagement : Screen("tasks", "Nhiệm vụ", Icons.Rounded.CheckCircle)
    object GroupPlanList : Screen("group_plans", "Kế hoạch nhóm", Icons.Rounded.Group)
    data class GroupPlanDetail(val planId: Long) : Screen(
        "group_plan_detail/$planId", "", Icons.Rounded.CheckCircle
    )
}
