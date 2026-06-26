package vn.edu.uit.devorbit.mobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.ui.theme.Amber
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.WeekDay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WeekTaskGrid(
    weekDates: List<WeekDay>,
    selectedDate: String?,
    tasksByDate: Map<String, Int>,
    completedByDate: Map<String, Int> = emptyMap(),
    onDayClick: (String?) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Nhiệm vụ trong tuần",
                        style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = CosmicTheme.colors.textPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weekDates.forEach { day ->
                    DayTaskSquare(
                        day = day,
                        taskCount = tasksByDate[day.date] ?: 0,
                        completedCount = completedByDate[day.date] ?: 0,
                        isSelected = day.date == selectedDate,
                        onClick = {
                            if (day.date == selectedDate) {
                                onDayClick(null)
                            } else {
                                onDayClick(day.date)
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Chọn một ngày để xem nhiệm vụ chi tiết",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary
            )
        }
    }
}

@Composable
fun DayTaskSquare(
    day: WeekDay,
    taskCount: Int,
    completedCount: Int = 0,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val total = day.activity?.tasksTotal ?: taskCount
    val completed = day.activity?.tasksCompleted ?: completedCount
    val isPast = try {
        java.time.LocalDate.parse(day.date).isBefore(java.time.LocalDate.now()) && !day.isToday
    } catch (_: Exception) { false }

    val bgColor = when {
        total == 0 -> Color.Transparent
        completed >= total -> Color(0xFF6BAF8D).copy(alpha = 0.55f)
        completed > 0 -> Color(0xFFD4A843).copy(alpha = 0.55f)
        day.isToday -> Amber.copy(alpha = 0.25f)
        isPast -> Color(0xFFD45B5B).copy(alpha = 0.55f)
        else -> Amber.copy(alpha = 0.55f)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(36.dp)
    ) {
        Text(
            text = day.label,
            style = CosmicTheme.typography.label.copy(fontSize = 9.sp),
            color = CosmicTheme.colors.textTertiary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (isSelected) CosmicTheme.colors.plasma.copy(alpha = 0.25f) else bgColor
                )
                .border(
                    1.dp,
                    when {
                        isSelected -> CosmicTheme.colors.plasma
                        day.isToday -> CosmicTheme.colors.plasma
                        taskCount > 0 -> Color.White.copy(alpha = 0.15f)
                        else -> Color.White.copy(alpha = 0.06f)
                    },
                    RoundedCornerShape(6.dp)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (taskCount > 0) {
                Text(
                    text = taskCount.toString(),
                    style = CosmicTheme.typography.label.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isSelected) Color.White else Color.White
                )
            }
        }
    }
}

data class MonthDayInfo(
    val dateStr: String,
    val dayOfMonth: Int,
    val isToday: Boolean,
    val isPast: Boolean = false,
    val hasTasks: Boolean,
    val completedCount: Int = 0,
    val totalCount: Int = 0
)

@Composable
fun MonthTaskGrid(
    year: Int,
    month: Int,
    tasksByDate: Map<String, Int>,
    completedByDate: Map<String, Int> = emptyMap(),
    selectedDate: String?,
    onDateClick: (String?) -> Unit,
    onNavigateMonth: (Int) -> Unit
) {
    val canGoPrev = year > 2026 || (year == 2026 && month > 6)
    val monthName = java.time.Month.of(month).getDisplayName(java.time.format.TextStyle.FULL, Locale("vi", "VN"))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (canGoPrev) {
                    IconButton(onClick = { onNavigateMonth(-1) }, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Tháng trước",
                            tint = CosmicTheme.colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(28.dp))
                }
                Text(
                    text = "$monthName $year",
                    style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = CosmicTheme.colors.textPrimary
                )
                IconButton(onClick = { onNavigateMonth(1) }, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Tháng sau",
                        tint = CosmicTheme.colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach { label ->
                    Text(
                        text = label,
                        style = CosmicTheme.typography.label.copy(fontSize = 10.sp),
                        color = CosmicTheme.colors.textTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val dayRows = remember(year, month, tasksByDate, completedByDate) {
                generateMonthDays(year, month, tasksByDate, completedByDate)
            }

            dayRows.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    week.forEach { cell ->
                        if (cell != null) {
                            MonthDayCell(
                                day = cell,
                                isSelected = cell.dateStr == selectedDate,
                                onClick = {
                                    if (cell.dateStr == selectedDate) {
                                        onDateClick(null)
                                    } else {
                                        onDateClick(cell.dateStr)
                                    }
                                }
                            )
                        } else {
                            Spacer(modifier = Modifier.width(36.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun MonthDayCell(
    day: MonthDayInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        day.totalCount == 0 -> Color.Transparent
        day.completedCount >= day.totalCount -> Color(0xFF6BAF8D).copy(alpha = 0.55f)
        day.completedCount > 0 -> Color(0xFFD4A843).copy(alpha = 0.55f)
        day.isToday -> Amber.copy(alpha = 0.25f)
        day.isPast -> Color(0xFFD45B5B).copy(alpha = 0.55f)
        else -> Amber.copy(alpha = 0.55f)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(36.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (isSelected) CosmicTheme.colors.plasma.copy(alpha = 0.25f) else bgColor
                )
                .border(
                    1.dp,
                    when {
                        isSelected -> CosmicTheme.colors.plasma
                        day.isToday -> CosmicTheme.colors.plasma
                        day.hasTasks -> CosmicTheme.colors.plasma.copy(alpha = 0.3f)
                        else -> Color.White.copy(alpha = 0.06f)
                    },
                    RoundedCornerShape(6.dp)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.dayOfMonth.toString(),
                style = CosmicTheme.typography.label.copy(fontSize = 11.sp),
                color = when {
                    isSelected -> Color.White
                    day.hasTasks -> Color.White
                    else -> CosmicTheme.colors.textPrimary
                }
            )
        }
    }
}

fun generateMonthDays(year: Int, month: Int, tasksByDate: Map<String, Int>, completedByDate: Map<String, Int>): List<List<MonthDayInfo?>> {
    val firstDay = LocalDate.of(year, month, 1)
    val daysInMonth = firstDay.lengthOfMonth()
    val startCol = (firstDay.dayOfWeek.value % 7)
    val today = LocalDate.now()
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val rows = mutableListOf<List<MonthDayInfo?>>()
    var cells = mutableListOf<MonthDayInfo?>()

    repeat(startCol) { cells.add(null) }

    for (day in 1..daysInMonth) {
        val date = LocalDate.of(year, month, day)
        val dateStr = date.format(dateFormat)
        val totalCount = tasksByDate[dateStr] ?: 0
        cells.add(
            MonthDayInfo(
                dateStr = dateStr,
                dayOfMonth = day,
                isToday = date == today,
                isPast = date.isBefore(today),
                hasTasks = totalCount > 0,
                completedCount = completedByDate[dateStr] ?: 0,
                totalCount = totalCount
            )
        )
        if (cells.size == 7) {
            rows.add(cells)
            cells = mutableListOf()
        }
    }

    if (cells.isNotEmpty()) {
        while (cells.size < 7) cells.add(null)
        rows.add(cells)
    }

    return rows
}

fun isTaskOnDate(epochMillis: Long, dateStr: String): Boolean {
    val date = Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    return date == dateStr
}

fun isDateStrInWeek(dateStr: String): Boolean {
    val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val now = LocalDate.now()
    val monday = now.with(java.time.DayOfWeek.MONDAY)
    val sunday = monday.plusDays(6)
    return date in monday..sunday
}
