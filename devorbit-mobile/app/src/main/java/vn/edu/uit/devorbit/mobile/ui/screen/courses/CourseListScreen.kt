package vn.edu.uit.devorbit.mobile.ui.screen.courses

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    viewModel: CourseViewModel,
    onCourseClick: (CourseEntity) -> Unit
) {
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val filterState by viewModel.courseSearchFilterState.collectAsStateWithLifecycle()
    val bookmarkedIds by viewModel.bookmarkedCourseIds.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = filterState.query,
            onValueChange = { viewModel.updateCourseSearch(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Tìm mã hoặc tên môn học...", color = CosmicTheme.colors.textTertiary) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = CosmicTheme.colors.textTertiary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CosmicTheme.colors.plasma,
                unfocusedBorderColor = CosmicTheme.colors.glassBorder,
                cursorColor = CosmicTheme.colors.plasma,
                focusedTextColor = CosmicTheme.colors.textPrimary,
                unfocusedTextColor = CosmicTheme.colors.textPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Subject type & semester dropdown filters
        var subjectExpanded by remember { mutableStateOf(false) }
        val selectedSubjectLabel = CourseSearchFilterState.subjectFilters
            .firstOrNull { it.value == filterState.subjectType }?.label ?: "Tất cả"

        var semesterExpanded by remember { mutableStateOf(false) }
        val selectedSemesterLabel = CourseSearchFilterState.semesterFilters
            .firstOrNull { it.value == filterState.semester }?.label ?: "Tất cả HK"

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = subjectExpanded,
                onExpandedChange = { subjectExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedSubjectLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Loại môn") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CosmicTheme.colors.plasma,
                        unfocusedBorderColor = CosmicTheme.colors.glassBorder,
                        focusedLabelColor = CosmicTheme.colors.plasma,
                        unfocusedLabelColor = CosmicTheme.colors.textTertiary,
                        cursorColor = CosmicTheme.colors.plasma,
                        focusedTextColor = CosmicTheme.colors.textPrimary,
                        unfocusedTextColor = CosmicTheme.colors.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = subjectExpanded,
                    onDismissRequest = { subjectExpanded = false }
                ) {
                    CourseSearchFilterState.subjectFilters.forEach { option ->
                        val isSelected = option.value == filterState.subjectType ||
                            (option.value == null && filterState.subjectType == null)
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option.label,
                                    color = if (isSelected) CosmicTheme.colors.plasma else CosmicTheme.colors.textPrimary
                                )
                            },
                            onClick = {
                                viewModel.selectCourseSubjectType(option.value)
                                subjectExpanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = semesterExpanded,
                onExpandedChange = { semesterExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedSemesterLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Học kỳ") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = semesterExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CosmicTheme.colors.aurora,
                        unfocusedBorderColor = CosmicTheme.colors.glassBorder,
                        focusedLabelColor = CosmicTheme.colors.aurora,
                        unfocusedLabelColor = CosmicTheme.colors.textTertiary,
                        cursorColor = CosmicTheme.colors.aurora,
                        focusedTextColor = CosmicTheme.colors.textPrimary,
                        unfocusedTextColor = CosmicTheme.colors.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = semesterExpanded,
                    onDismissRequest = { semesterExpanded = false }
                ) {
                    CourseSearchFilterState.semesterFilters.forEach { option ->
                        val isSelected = option.value == filterState.semester ||
                            (option.value == null && filterState.semester == null)
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option.label,
                                    color = if (isSelected) CosmicTheme.colors.aurora else CosmicTheme.colors.textPrimary
                                )
                            },
                            onClick = {
                                viewModel.selectSemester(option.value)
                                semesterExpanded = false
                            }
                        )
                    }
                }
            }
        }

        if (courses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không tìm thấy môn học phù hợp",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textTertiary
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(courses, key = { it.id }) { course ->
                CourseListItem(
                    course = course,
                    isBookmarked = course.id in bookmarkedIds,
                    onClick = { onCourseClick(course) },
                    onBookmarkClick = { viewModel.toggleCourseBookmark(course) }
                )
            }
        }
    }
}

private fun deriveDifficulty(credits: Int, semester: Int?, loaiMonHoc: String?): String {
    var score = credits
    if (semester != null && semester >= 3) score += 1
    when (loaiMonHoc) {
        "CHUYEN_NGANH" -> score += 2
        "CO_SO" -> score += 1
    }
    return when {
        score >= 4 -> "Khó"
        score >= 2 -> "Trung bình"
        else -> "Dễ"
    }
}
private fun formatSubjectType(raw: String?): String? = when (raw) {
    "DAI_CUONG" -> "Đại cương"
    "CO_SO" -> "Cơ sở ngành"
    "CHUYEN_NGANH" -> "Chuyên ngành"
    "TU_CHON" -> "Tự chọn"
    else -> raw
}

@Composable
fun CourseListItem(course: CourseEntity, isBookmarked: Boolean = false, onClick: () -> Unit, onBookmarkClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.maMH,
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.plasma
                    )
                    Text(
                        text = course.tenMH,
                        style = CosmicTheme.typography.body,
                        color = CosmicTheme.colors.textPrimary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBookmarkClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = if (isBookmarked) "Bỏ lưu" else "Lưu",
                            tint = if (isBookmarked) CosmicTheme.colors.plasma else CosmicTheme.colors.textTertiary.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "${course.repoCount} repo · ${course.credits} TC",
                        style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.SemiBold),
                        color = CosmicTheme.colors.textTertiary
                    )
                }
            }

            val subjectLabel = formatSubjectType(course.loaiMonHoc)
            val difficulty = deriveDifficulty(course.credits, course.semester, course.loaiMonHoc)
            val difficultyColor = when (difficulty) {
                "Khó" -> CosmicTheme.colors.supernova
                "Trung bình" -> CosmicTheme.colors.plasma
                else -> CosmicTheme.colors.aurora
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            difficulty,
                            style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Medium),
                            color = difficultyColor
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        borderColor = difficultyColor.copy(alpha = 0.3f),
                        enabled = true
                    ),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = difficultyColor.copy(alpha = 0.08f)
                    )
                )
                if (course.semester != null) {
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                "HK ${course.semester}",
                                style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Medium),
                                color = CosmicTheme.colors.plasma
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            borderColor = CosmicTheme.colors.plasma.copy(alpha = 0.3f),
                            enabled = true
                        ),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = CosmicTheme.colors.plasma.copy(alpha = 0.08f)
                        )
                    )
                }
                if (subjectLabel != null) {
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                subjectLabel,
                                style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Medium),
                                color = CosmicTheme.colors.aurora
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            borderColor = CosmicTheme.colors.aurora.copy(alpha = 0.3f),
                            enabled = true
                        ),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = CosmicTheme.colors.aurora.copy(alpha = 0.08f)
                        )
                    )
                }
            }
        }
    }
}
