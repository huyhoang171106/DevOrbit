package vn.edu.uit.devorbit.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.design.OrbitColors

/**
 * A single day in the study timeline.
 */
data class TimelineDay(
    val dayOfWeek: String,
    val date: Int,
    val isToday: Boolean = false,
    val sessions: List<TimelineSession> = emptyList(),
)

data class TimelineSession(
    val id: String,
    val title: String,
    val duration: String,
    val completed: Boolean = false,
    val subject: String = "",
    val color: androidx.compose.ui.graphics.Color = OrbitColors.PrimaryElectricBlue,
)

/**
 * Weekly study timeline with daily sessions.
 */
@Composable
fun StudyTimeline(
    days: List<TimelineDay>,
    modifier: Modifier = Modifier,
    onSessionClick: (String) -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        days.forEach { day ->
            val isToday = day.isToday
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isToday) OrbitColors.SurfaceInteractive.copy(alpha = 0.5f)
                        else OrbitColors.SurfaceBase
                    )
                    .border(
                        width = if (isToday) 1.dp else 0.dp,
                        color = if (isToday) OrbitColors.PrimaryElectricBlue.copy(alpha = 0.3f)
                        else OrbitColors.BorderSubtle.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = day.dayOfWeek.take(3),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isToday) OrbitColors.PrimaryElectricBlue else OrbitColors.TextMuted,
                    fontFamily = FontFamily.Monospace,
                )

                Text(
                    text = day.date.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isToday) OrbitColors.TextPrimary else OrbitColors.TextSecondary,
                )

                Spacer(Modifier.height(4.dp))

                day.sessions.forEach { session ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (session.completed) session.color
                                else session.color.copy(alpha = 0.3f)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onSessionClick(session.id)
                                },
                            ),
                    )
                }

                if (day.sessions.isEmpty()) {
                    Spacer(Modifier.height(3.dp))
                }
            }
        }
    }
}
