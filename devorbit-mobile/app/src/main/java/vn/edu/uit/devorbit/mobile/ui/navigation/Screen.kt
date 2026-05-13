package vn.edu.uit.devorbit.mobile.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Bridge", Icons.Rounded.Home)
    object Courses : Screen("courses", "Galaxy", Icons.Rounded.Search)
    object Progress : Screen("progress", "Track", Icons.Rounded.List)
    object Execution : Screen("execution", "Plan", Icons.Rounded.CheckCircle)
    object Copilot : Screen("copilot", "Risk", Icons.Rounded.Face)
}
