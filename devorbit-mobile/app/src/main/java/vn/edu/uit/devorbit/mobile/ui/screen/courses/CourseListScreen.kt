package vn.edu.uit.devorbit.mobile.ui.screen.courses

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CourseListScreen(
    viewModel: CourseViewModel,
    onCourseClick: (CourseEntity) -> Unit
) {
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val filterState by viewModel.courseSearchFilterState.collectAsStateWithLifecycle()

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

        // Subject type filters
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CourseSearchFilterState.subjectFilters.forEach { option ->
                FilterChip(
                    selected = filterState.subjectType == option.value || (option.value == null && filterState.subjectType == null),
                    onClick = { viewModel.selectCourseSubjectType(option.value) },
                    label = { Text(option.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.14f),
                        selectedLabelColor = CosmicTheme.colors.plasma,
                        labelColor = CosmicTheme.colors.textSecondary,
                        containerColor = CosmicTheme.colors.nebula
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = filterState.subjectType == option.value || (option.value == null && filterState.subjectType == null),
                        borderColor = CosmicTheme.colors.glassBorder,
                        selectedBorderColor = CosmicTheme.colors.plasma
                    )
                )
            }
        }

        // Semester dropdown filter
        var semesterExpanded by remember { mutableStateOf(false) }
        val selectedSemesterLabel = CourseSearchFilterState.semesterFilters
            .firstOrNull { it.value == filterState.semester }?.label ?: "Tất cả HK"

        ExposedDropdownMenuBox(
            expanded = semesterExpanded,
            onExpandedChange = { semesterExpanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = selectedSemesterLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Học kỳ") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = semesterExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
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
                CourseListItem(course = course, onClick = { onCourseClick(course) })
            }
        }
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
fun CourseListItem(course: CourseEntity, onClick: () -> Unit) {
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
                Text(
                    text = "${course.credits} TC",
                    style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.SemiBold),
                    color = CosmicTheme.colors.textTertiary
                )
            }

            val subjectLabel = formatSubjectType(course.loaiMonHoc)
            if (course.semester != null || subjectLabel != null) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
}
