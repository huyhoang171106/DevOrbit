package vn.edu.uit.devorbit.mobile.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Tổng quan", Icons.Rounded.Home)
    object Courses : Screen("courses", "Môn học", Icons.Rounded.List)
    object Knowledge : Screen("knowledge", "Kiến thức", Icons.Rounded.Star)
    object Explore : Screen("explore", "Khám phá", Icons.Rounded.Search)
    object Plan : Screen("plan", "Kế hoạch", Icons.Rounded.DateRange)
    object Profile : Screen("profile", "Cá nhân", Icons.Rounded.Person)
}
