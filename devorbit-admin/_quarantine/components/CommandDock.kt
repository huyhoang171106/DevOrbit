package vn.edu.uit.devorbit.admin.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.design.OrbitColors
import vn.edu.uit.devorbit.admin.design.OrbitShapes

data class DockItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
)

val dockItems = listOf(
    DockItem("today", "Today", Icons.Default.Today),
    DockItem("subjects", "Subjects", Icons.Default.LibraryBooks),
    DockItem("tutor", "AI Tutor", Icons.Default.Bolt),
    DockItem("studyplan", "Study Plan", Icons.Default.Book),
    DockItem("profile", "Profile", Icons.Default.Person),
)

@Composable
fun CommandDock(
    activeDestination: String,
    onDestinationSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    dockShape: RoundedCornerShape = OrbitShapes.navigationDock,
) {
    val haptic = LocalHapticFeedback.current

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .shadow(24.dp, dockShape, ambientColor = OrbitColors.PrimaryElectricBlue.copy(alpha = 0.08f))
                    .background(OrbitColors.SurfaceRaised.copy(alpha = 0.96f), dockShape)
                    .border(
                        width = 0.5.dp,
                        color = OrbitColors.BorderSubtle.copy(alpha = 0.5f),
                        shape = dockShape,
                    )
                    .clip(dockShape)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    dockItems.forEach { item ->
                        DockItemButton(
                            item = item,
                            isActive = item.id == activeDestination,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onDestinationSelected(item.id)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DockItemButton(
    item: DockItem,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) OrbitColors.SurfaceInteractive else Color.Transparent,
        animationSpec = tween(200),
        label = "dockBg",
    )
    val contentColor by animateColorAsState(
        targetValue = if (isActive) OrbitColors.PrimaryElectricBlue else OrbitColors.TextMuted,
        animationSpec = tween(200),
        label = "dockContent",
    )
    val targetWidth by animateDpAsState(
        targetValue = if (isActive) 72.dp else 40.dp,
        animationSpec = tween(220),
        label = "dockWidth",
    )
    val iconScale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = tween(200),
        label = "dockIconScale",
    )

    Box(
        modifier = Modifier
            .width(targetWidth)
            .height(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = contentColor,
                modifier = Modifier
                    .size(20.dp)
                    .scale(iconScale),
            )
            if (isActive) {
                Spacer(Modifier.width(4.dp))
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor,
                )
            }
        }
    }
}
