package vn.edu.uit.devorbit.mobile.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Tổng quan", Icons.Rounded.Home)
    object Repos : Screen("repos", "Môn học", Icons.Rounded.List)
    object Explore : Screen("explore", "Khám phá", Icons.Rounded.Search)
    object Plan : Screen("plan", "Kế hoạch", Icons.Rounded.DateRange)
    object Gpa : Screen("gpa", "GPA", Icons.Rounded.Star)
    object Profile : Screen("profile", "Cá nhân", Icons.Rounded.Person)

    companion object {
        val navItems: List<Screen>
            get() = listOf(
                Dashboard,
                Repos,
                Explore,
                Plan,
                Gpa,
                Profile
            )
    }
}
