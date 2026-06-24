package vn.edu.uit.devorbit.admin.core.designsystem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.ui.theme.*

// ── Navigation Groups ─────────────────────────────────────────────────────────

sealed class NavGroup(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
) {
    // 1. Điều hành
    object Command : NavGroup("command", "Điều hành", Icons.Rounded.Dashboard, Icons.Rounded.Dashboard)

    // 2. Nội dung
    object Content : NavGroup("content", "Nội dung", Icons.Rounded.MenuBook, Icons.Rounded.MenuBook)

    // 3. Kiểm duyệt
    object Moderation : NavGroup("moderation", "Kiểm duyệt", Icons.Rounded.RateReview, Icons.Rounded.RateReview)

    // 4. Người dùng
    object Users : NavGroup("users", "Người dùng", Icons.Rounded.People, Icons.Rounded.People)

    // 5. Thêm
    object More : NavGroup("more", "Thêm", Icons.Rounded.MoreHoriz, Icons.Rounded.MoreHoriz)

    companion object {
        val items = listOf(Command, Content, Moderation, Users, More)
    }
}

// ── Admin Top Bar ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar(
    title: String,
    onNotificationClick: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    unreadCount: Int = 0,
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (showBack && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Quay lại")
                }
            }
        },
        actions = {
            actions()
            if (onNotificationClick != null) {
                IconButton(onClick = onNotificationClick) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(
                                    containerColor = Danger,
                                    contentColor = Color.White,
                                ) {
                                    Text(
                                        text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Rounded.Notifications, contentDescription = "Thông báo")
                    }
                }
            }
            if (onLogout != null) {
                IconButton(onClick = onLogout) {
                    Icon(Icons.Rounded.Logout, contentDescription = "Đăng xuất")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Surface,
            scrolledContainerColor = Surface,
        ),
    )
}

// ── Admin Bottom Nav ──────────────────────────────────────────────────────────

@Composable
fun AdminBottomNav(
    selectedGroup: NavGroup,
    onGroupSelected: (NavGroup) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Surface,
        tonalElevation = 0.dp,
    ) {
        NavGroup.items.forEach { group ->
            val selected = group == selectedGroup
            NavigationBarItem(
                selected = selected,
                onClick = { onGroupSelected(group) },
                icon = {
                    Icon(
                        if (selected) group.selectedIcon else group.icon,
                        contentDescription = group.label,
                    )
                },
                label = {
                    Text(
                        group.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = UITBlue,
                    selectedTextColor = UITBlue,
                    indicatorColor = UITBlueSoft,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                ),
            )
        }
    }
}

// ── Admin Navigation Rail ─────────────────────────────────────────────────────

@Composable
fun AdminNavigationRail(
    selectedGroup: NavGroup,
    onGroupSelected: (NavGroup) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRail(
        modifier = modifier,
        containerColor = Surface,
        header = {
            Spacer(Modifier.height(8.dp))
        },
    ) {
        Spacer(Modifier.height(8.dp))
        NavGroup.items.forEach { group ->
            val selected = group == selectedGroup
            NavigationRailItem(
                selected = selected,
                onClick = { onGroupSelected(group) },
                icon = {
                    Icon(
                        if (selected) group.selectedIcon else group.icon,
                        contentDescription = group.label,
                    )
                },
                label = {
                    Text(
                        group.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = UITBlue,
                    selectedTextColor = UITBlue,
                    indicatorColor = UITBlueSoft,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                ),
            )
        }
    }
}

// ── Adaptive Shell ────────────────────────────────────────────────────────────

@Composable
fun AdminScaffold(
    selectedGroup: NavGroup,
    onGroupSelected: (NavGroup) -> Unit,
    topBarTitle: String,
    modifier: Modifier = Modifier,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    unreadCount: Int = 0,
    onNotificationClick: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    topBarActions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isExpanded = windowSizeClass.windowWidthSizeClass.toString() == "Expanded"

    if (isExpanded) {
        Row(modifier = modifier.fillMaxSize()) {
            AdminNavigationRail(
                selectedGroup = selectedGroup,
                onGroupSelected = onGroupSelected,
            )
            Scaffold(
                modifier = Modifier.weight(1f),
                topBar = {
                    if (showTopBar) {
                        AdminTopBar(
                            title = topBarTitle,
                            onNotificationClick = onNotificationClick,
                            onLogout = onLogout,
                            unreadCount = unreadCount,
                            actions = topBarActions,
                        )
                    }
                },
                containerColor = AppBackground,
            ) { padding ->
                content(padding)
            }
        }
    } else {
        Scaffold(
            modifier = modifier,
            topBar = {
                if (showTopBar) {
                    AdminTopBar(
                        title = topBarTitle,
                        onNotificationClick = onNotificationClick,
                        onLogout = onLogout,
                        unreadCount = unreadCount,
                        actions = topBarActions,
                    )
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it }),
                ) {
                    AdminBottomNav(
                        selectedGroup = selectedGroup,
                        onGroupSelected = onGroupSelected,
                    )
                }
            },
            containerColor = AppBackground,
        ) { padding ->
            content(padding)
        }
    }
}
