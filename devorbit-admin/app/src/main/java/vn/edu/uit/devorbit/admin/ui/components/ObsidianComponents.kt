package vn.edu.uit.devorbit.admin.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.ui.theme.*

// ══════════════════════════════════════════════════════════════════════════════
// OBSIDIAN CONTROL CENTER — Reusable UI Primitives
// ══════════════════════════════════════════════════════════════════════════════

// ── KPI Stat Card ────────────────────────────────────────────────────────────
@Composable
fun ObsidianStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    trend: String? = null,
    trendUp: Boolean = true
) {
    Card(
        modifier = modifier,
        shape = ObsidianShape.md,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(ObsidianShape.sm)
                            .background(accentColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                }
                Text(label, style = ObsidianType.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text(value, style = ObsidianType.headlineLarge, color = MaterialTheme.colorScheme.onSurface)
            if (trend != null) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (trendUp) Icons.Rounded.TrendingUp else Icons.Rounded.TrendingDown,
                        contentDescription = null,
                        tint = if (trendUp) ObsidianPalette.Green500 else ObsidianPalette.Red500,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(trend, style = ObsidianType.labelSmall, color = if (trendUp) ObsidianPalette.Green500 else ObsidianPalette.Red500)
                }
            }
        }
    }
}

// ── Section Header ───────────────────────────────────────────────────────────
@Composable
fun ObsidianSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = ObsidianType.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        action?.invoke()
    }
}

// ── Status Badge ─────────────────────────────────────────────────────────────
@Composable
fun ObsidianBadge(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    val useSolidBackground = color == MaterialTheme.colorScheme.primary ||
        color == ObsidianPalette.Blue500 ||
        color == ObsidianPalette.Blue700 ||
        color == ObsidianPalette.Green500 ||
        color == ObsidianPalette.Red500 ||
        color == Info ||
        color == MaterialTheme.colorScheme.onSurfaceVariant ||
        color == ObsidianPalette.Gray500
    val backgroundColor = if (useSolidBackground) color else color.copy(alpha = 0.12f)
    val contentColor = if (useSolidBackground) Color.White else color

    Box(
        modifier = modifier
            .clip(ObsidianShape.full)
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, style = ObsidianType.labelSmall, color = contentColor)
    }
}

@Composable
fun ObsidianButtonText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = ObsidianType.labelMedium
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = LocalContentColor.current
    )
}

// ── Avatar ───────────────────────────────────────────────────────────────────
@Composable
fun ObsidianAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Int = 40,
    icon: ImageVector? = null
) {
    val initials = name.split(" ").take(2).joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
    val colors = listOf(
        ObsidianPalette.Blue500, ObsidianPalette.Green500, ObsidianPalette.Amber500,
        ObsidianPalette.Red500, ObsidianPalette.Blue700
    )
    val color = colors[name.hashCode().let { if (it < 0) -it else it } % colors.size]

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size((size * 0.5).dp))
        } else {
            Text(initials, style = ObsidianType.labelMedium.copy(fontWeight = FontWeight.Bold), color = color)
        }
    }
}

// ── Search Bar ───────────────────────────────────────────────────────────────
@Composable
fun ObsidianSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Tìm kiếm...",
    active: Boolean = false
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        placeholder = { Text(placeholder, style = ObsidianType.bodyMedium) },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Rounded.Close, contentDescription = "Xoá", modifier = Modifier.size(16.dp))
                }
            }
        },
        singleLine = true,
        shape = ObsidianShape.sm,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        textStyle = ObsidianType.bodyMedium
    )
}

// ── Empty State ──────────────────────────────────────────────────────────────
@Composable
fun ObsidianEmptyState(
    message: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector = Icons.Rounded.Inbox
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(message, style = ObsidianType.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (subtitle != null) {
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = ObsidianType.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }
}

// ── Loading State ────────────────────────────────────────────────────────────
@Composable
fun ObsidianLoadingBox(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
    }
}

// ── Skeleton Shimmer ─────────────────────────────────────────────────────────
@Composable
fun ObsidianSkeleton(
    modifier: Modifier = Modifier,
    width: Float = 1f,
    height: Int = 16
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha = transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "skeletonAlpha"
    )
    Box(
        modifier = modifier
            .fillMaxWidth(width)
            .height(height.dp)
            .clip(ObsidianShape.sm)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha.value))
    )
}

// ── Confirm Dialog ───────────────────────────────────────────────────────────
@Composable
fun ObsidianConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmLabel: String = "Xác nhận",
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = ObsidianType.headlineSmall) },
        text = { Text(message, style = ObsidianType.bodyMedium) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isDestructive) ObsidianPalette.Red500 else MaterialTheme.colorScheme.primary
                )
            ) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Huỷ") }
        }
    )
}

// ── Data Row ─────────────────────────────────────────────────────────────────
@Composable
fun ObsidianDataRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = ObsidianType.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = ObsidianType.bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}

// ── Page Header ──────────────────────────────────────────────────────────────
@Composable
fun ObsidianPageHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        navigationIcon?.invoke()
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = ObsidianType.headlineLarge, color = MaterialTheme.colorScheme.onSurface)
            if (subtitle != null) {
                Text(subtitle, style = ObsidianType.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), content = actions)
    }
}

// ── Divider ──────────────────────────────────────────────────────────────────
@Composable
fun ObsidianDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant,
        thickness = 0.5.dp
    )
}
