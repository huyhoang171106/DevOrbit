package vn.edu.uit.devorbit.mobile.ui.screen.courses

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.SemesterPlannerViewModel
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterPlannerScreen(
    viewModel: SemesterPlannerViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var expandedSemester by remember { mutableIntStateOf(1) }
    var showAddDialog by remember { mutableStateOf(false) }
    var addTargetSemester by remember { mutableIntStateOf(1) }
    var searchQuery by remember { mutableStateOf("") }
    var moveCourseId by remember { mutableStateOf<Long?>(null) }
    var moveFromSemester by remember { mutableIntStateOf(1) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = CosmicTheme.colors.textSecondary)
            }
            Text("Lập kế hoạch học tập", style = CosmicTheme.typography.body, color = CosmicTheme.colors.textSecondary)
        }

        if (state.loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CosmicTheme.colors.plasma, strokeWidth = 2.dp)
            }
        } else {
            // Summary
            val totalCredits = (1..8).sumOf { viewModel.getTotalCredits(it) }
            val totalCourses = (1..8).sumOf { viewModel.getCourseCount(it) }

            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                color = CosmicTheme.colors.nebula,
                border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("8", style = CosmicTheme.typography.metric.copy(fontSize = 24.sp), color = CosmicTheme.colors.plasma)
                        Text("Học kỳ", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$totalCourses", style = CosmicTheme.typography.metric.copy(fontSize = 24.sp), color = CosmicTheme.colors.aurora)
                        Text("Môn học", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$totalCredits", style = CosmicTheme.typography.metric.copy(fontSize = 24.sp), color = CosmicTheme.colors.supernova)
                        Text("Tín chỉ", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                    }
                }
            }

            // Semester list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..8).forEach { sem ->
                    val courses = viewModel.getCoursesForSemester(sem)
                    val credits = viewModel.getTotalCredits(sem)
                    val isExpanded = expandedSemester == sem
                    val isOver = credits > 22
                    val isLow = credits in 1..13

                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth().animateContentSize(),
                            shape = RoundedCornerShape(14.dp),
                            color = CosmicTheme.colors.nebula,
                            border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                        ) {
                            Column(modifier = Modifier.padding(4.dp)) {
                                // Header row
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        expandedSemester = if (isExpanded) -1 else sem
                                    }.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Học kỳ $sem", style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold), color = CosmicTheme.colors.textPrimary)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("${courses.size} môn · $credits TC", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                                            if (isOver) {
                                                Spacer(Modifier.width(6.dp))
                                                Text("Nhiều", color = CosmicTheme.colors.supernova, style = CosmicTheme.typography.label.copy(fontSize = 10.sp))
                                            } else if (isLow) {
                                                Spacer(Modifier.width(6.dp))
                                                Text("Ít", color = CosmicTheme.colors.plasma, style = CosmicTheme.typography.label.copy(fontSize = 10.sp))
                                            }
                                        }
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Column(horizontalAlignment = Alignment.End) {
                                        Box(
                                            modifier = Modifier.width(60.dp).height(6.dp).clip(RoundedCornerShape(3.dp)).background(CosmicTheme.colors.glassBorder)
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxHeight().fillMaxWidth(fraction = min(1f, credits.toFloat() / 22f))
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(when { isOver -> CosmicTheme.colors.supernova; credits >= 18 -> CosmicTheme.colors.aurora; else -> CosmicTheme.colors.plasma })
                                            )
                                        }
                                        Text("$credits/22 TC", style = CosmicTheme.typography.label.copy(fontSize = 9.sp), color = CosmicTheme.colors.textTertiary)
                                    }
                                }

                                // Expanded content
                                AnimatedVisibility(visible = isExpanded) {
                                    Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)) {
                                        if (courses.isEmpty()) {
                                            Text("Chưa có môn học nào", modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                                style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary, textAlign = TextAlign.Center)
                                        } else {
                                            courses.forEachIndexed { index, course ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text(course.maMH, style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold), color = CosmicTheme.colors.plasma)
                                                            Spacer(Modifier.width(6.dp))
                                                            Text(course.tenMH, style = CosmicTheme.typography.body, color = CosmicTheme.colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                        }
                                                    }
                                                    Text("${course.credits} TC", style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.SemiBold), color = CosmicTheme.colors.textTertiary, modifier = Modifier.padding(horizontal = 8.dp))
                                                    IconButton(onClick = { moveCourseId = course.id; moveFromSemester = sem }, modifier = Modifier.size(32.dp)) {
                                                        Icon(Icons.Default.MoveDown, contentDescription = "Chuyển", tint = CosmicTheme.colors.plasma.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                                    }
                                                    IconButton(onClick = { viewModel.removeCourse(course.id) }, modifier = Modifier.size(32.dp)) {
                                                        Icon(Icons.Default.Close, contentDescription = "Bỏ môn", tint = CosmicTheme.colors.supernova.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                                if (index < courses.lastIndex) {
                                                    HorizontalDivider(color = CosmicTheme.colors.glassBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
                                                }
                                            }
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        OutlinedButton(
                                            onClick = { addTargetSemester = sem; searchQuery = ""; showAddDialog = true },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CosmicTheme.colors.plasma),
                                            border = ButtonDefaults.outlinedButtonBorder
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Thêm môn", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }

    // Add course dialog
    if (showAddDialog) {
        val currentCourses = viewModel.getCoursesForSemester(addTargetSemester)
        val currentCredits = viewModel.getTotalCredits(addTargetSemester)
        val availableCourses = viewModel.getAvailableCourses()
            .filter {
                searchQuery.isBlank() || it.maMH.contains(searchQuery, ignoreCase = true) || it.tenMH.contains(searchQuery, ignoreCase = true)
            }
            .sortedBy { it.maMH }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Thêm môn - Học kỳ $addTargetSemester", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("$currentCredits/22 TC · ${currentCourses.size} môn", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Tìm mã hoặc tên môn...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    if (currentCredits >= 22) {
                        Text("Học kỳ này đã đạt tối đa 22 TC!", style = CosmicTheme.typography.label, color = CosmicTheme.colors.supernova, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        items(availableCourses) { course ->
                            val wouldExceed = currentCredits + course.credits > 22
                            Surface(
                                modifier = Modifier.fillMaxWidth().clickable(enabled = !wouldExceed) { viewModel.addCourseToSemester(course.id, addTargetSemester) },
                                shape = RoundedCornerShape(10.dp),
                                color = CosmicTheme.colors.nebula
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(course.maMH, style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold), color = if (wouldExceed) CosmicTheme.colors.textTertiary else CosmicTheme.colors.plasma)
                                        Text(course.tenMH, style = CosmicTheme.typography.body, color = if (wouldExceed) CosmicTheme.colors.textTertiary else CosmicTheme.colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Text("${course.credits} TC", style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.SemiBold), color = if (wouldExceed) CosmicTheme.colors.supernova else CosmicTheme.colors.aurora)
                                    if (wouldExceed) {
                                        Spacer(Modifier.width(4.dp))
                                        Text("Quá 22", style = CosmicTheme.typography.label.copy(fontSize = 10.sp), color = CosmicTheme.colors.supernova)
                                    }
                                }
                            }
                        }
                        if (availableCourses.isEmpty()) {
                            item {
                                Text(if (searchQuery.isBlank()) "Tất cả môn đã được thêm" else "Không tìm thấy",
                                    modifier = Modifier.padding(16.dp), style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAddDialog = false }) { Text("Đóng") } }
        )
    }

    // Move course dialog
    moveCourseId?.let { cId ->
        AlertDialog(
            onDismissRequest = { moveCourseId = null },
            title = { Text("Chuyển môn", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Chọn học kỳ mới:", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                    (1..8).filter { it != moveFromSemester }.forEach { sem ->
                        val count = viewModel.getCourseCount(sem)
                        val credits = viewModel.getTotalCredits(sem)
                        Surface(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickable {
                                viewModel.moveCourse(cId, sem)
                                moveCourseId = null
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = CosmicTheme.colors.nebula,
                            border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("Học kỳ $sem", style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Medium), color = CosmicTheme.colors.textPrimary, modifier = Modifier.weight(1f))
                                Text("$count môn · $credits TC", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { moveCourseId = null }) { Text("Hủy") } }
        )
    }
}
