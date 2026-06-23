package vn.edu.uit.devorbit.admin.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.design.OrbitColors
import vn.edu.uit.devorbit.admin.design.OrbitShapes

/**
 * Compact subject progress node card with mini Canvas visual.
 */
@Composable
fun SubjectProgressNode(
    title: String,
    code: String,
    progress: Float,
    status: String,
    color: Color,
    nextTask: String,
    estimatedTime: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    featured: Boolean = false,
) {
    val haptic = LocalHapticFeedback.current
    val borderColor by animateColorAsState(
        targetValue = if (featured) color else color.copy(alpha = 0.3f),
        animationSpec = tween(300),
        label = "subjectBorder",
    )

    val shape = if (featured) OrbitShapes.contentPanel else OrbitShapes.technicalRow
    val bgColor = OrbitColors.SurfaceBase

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
            )
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            // Mini Canvas visual
            Box(
                modifier = Modifier
                    .size(if (featured) 64.dp else 44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(OrbitColors.BackgroundNavy),
                contentAlignment = Alignment.Center,
            ) {
                SubjectMiniCanvas(
                    progress = progress,
                    color = color,
                    size = if (featured) 40 else 28,
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = code,
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        fontFamily = FontFamily.Monospace,
                    )
                    Spacer(Modifier.width(8.dp))
                    if (status == "active" || status == "paused" || status == "completed") {
                        StatusChip(
                            text = when (status) {
                                "active" -> "Active"
                                "paused" -> "Paused"
                                "completed" -> "Done"
                                else -> status
                            },
                            color = when (status) {
                                "active" -> OrbitColors.CyanSignal
                                "paused" -> OrbitColors.Warning
                                "completed" -> OrbitColors.Success
                                else -> OrbitColors.TextMuted
                            },
                        )
                    }
                }

                Text(
                    text = title,
                    style = if (featured) MaterialTheme.typography.titleSmall
                    else MaterialTheme.typography.bodyLarge,
                    color = OrbitColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Next: $nextTask",
                        style = MaterialTheme.typography.bodySmall,
                        color = OrbitColors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = estimatedTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = OrbitColors.TextMuted,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectMiniCanvas(
    progress: Float,
    color: Color,
    size: Int,
) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val pad = 2f
        val w = size.toFloat() - pad * 2
        val h = size.toFloat() - pad * 2
        val barH = h * 0.3f

        // Pulse dots
        val dots = 4
        for (i in 0 until dots) {
            val dotX = pad + w * (i.toFloat() / (dots - 1).coerceAtLeast(1))
            val isFilled = i.toFloat() / (dots - 1).coerceAtLeast(1) <= progress
            drawCircle(
                color = if (isFilled) color else color.copy(alpha = 0.15f),
                radius = 2.5f,
                center = Offset(dotX, pad + barH + 6f),
            )
        }

        // Progress bar
        drawRoundRect(
            color = color.copy(alpha = 0.15f),
            topLeft = Offset(pad, pad),
            size = Size(w, barH),
            cornerRadius = CornerRadius(2f),
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(pad, pad),
            size = Size(w * progress, barH),
            cornerRadius = CornerRadius(2f),
        )
    }
}
