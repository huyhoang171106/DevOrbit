package vn.edu.uit.devorbit.mobile.ui.screen.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.model.domain.EventType
import vn.edu.uit.devorbit.mobile.model.domain.Milestone
import vn.edu.uit.devorbit.mobile.model.domain.SemesterTimeline
import vn.edu.uit.devorbit.mobile.model.domain.TimelineEvent
import vn.edu.uit.devorbit.mobile.ui.components.CosmicBackground
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.*

private fun EventType.color(): Color = when (this) {
    EventType.EXAM -> Color(0xFFFF6B6B)
    EventType.ASSIGNMENT -> CosmicNebulaPurple
    EventType.DEADLINE -> Color(0xFFFFB74D)
    EventType.BREAK -> CosmicGlowBlue
    EventType.MILESTONE -> CosmicGlowPurple
}

private fun EventType.label(): String = when (this) {
    EventType.EXAM -> "Thi"
    EventType.ASSIGNMENT -> "Bài tập"
    EventType.DEADLINE -> "Hạn chót"
    EventType.BREAK -> "Nghỉ"
    EventType.MILESTONE -> "Mốc"
}

@Composable
fun SemesterTimelineScreen(timeline: SemesterTimeline?) {
    CosmicBackground {
        if (timeline == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có dữ liệu timeline",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
            return@CosmicBackground
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = timeline.semesterName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${timeline.weeks.size} tuần",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            // Horizontal scrollable week timeline
            item {
                TimelineBar(weeks = timeline.weeks)
            }

            // Week details
            items(timeline.weeks, key = { it.weekNumber }) { week ->
                WeekEventsCard(weekNumber = week.weekNumber, label = week.label, events = week.events)
            }

            // Milestones section
            if (timeline.milestones.isNotEmpty()) {
                item {
                    Text(
                        text = "Cột mốc quan trọng",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(timeline.milestones) { milestone ->
                    MilestoneCard(milestone)
                }
            }
        }
    }
}

@Composable
private fun TimelineBar(
    weeks: List<vn.edu.uit.devorbit.mobile.model.domain.TimelineWeek>
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = "Dòng thời gian",
                style = MaterialTheme.typography.titleSmall,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                weeks.forEach { week ->
                    val hasEvents = week.events.isNotEmpty()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(48.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .then(
                                    if (hasEvents) Modifier
                                        .background(
                                            color = CosmicNebulaPurple.copy(alpha = 0.25f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    else Modifier
                                        .background(
                                            color = GlassWhite,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${week.weekNumber}",
                                fontSize = 13.sp,
                                fontWeight = if (hasEvents) FontWeight.Bold else FontWeight.Normal,
                                color = if (hasEvents) CosmicNebulaPurple else TextSecondary
                            )
                        }
                        Text(
                            text = week.label.take(5),
                            fontSize = 9.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekEventsCard(
    weekNumber: Int,
    label: String,
    events: List<TimelineEvent>
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "$label (Tuần $weekNumber)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            if (events.isEmpty()) {
                Text(
                    text = "Không có sự kiện",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            } else {
                events.forEach { event ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(32.dp)
                                .background(
                                    color = event.type.color(),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    color = event.type.color().copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = event.type.label(),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        color = event.type.color(),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                if (event.subjectName.isNotEmpty()) {
                                    Text(
                                        text = event.subjectName,
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MilestoneCard(milestone: Milestone) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .padding(0.dp)
                    .background(CosmicGlowPurple, RoundedCornerShape(5.dp))
            )
            Column {
                Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                if (milestone.description.isNotEmpty()) {
                    Text(
                        text = milestone.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
