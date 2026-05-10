package vn.edu.uit.devorbit.mobile.ui.navigation

sealed class BottomNavItem(val route: String, val label: String, val icon: String) {
    data object Dashboard : BottomNavItem("dashboard", "Tổng quan", "🏠")
    data object Knowledge : BottomNavItem("knowledge", "Kiến thức", "🧠")
    data object Plan : BottomNavItem("plan", "Kế hoạch", "📋")
    data object Analytics : BottomNavItem("analytics", "Phân tích", "📊")
    data object Risk : BottomNavItem("risk", "Rủi ro", "⚡")
}
