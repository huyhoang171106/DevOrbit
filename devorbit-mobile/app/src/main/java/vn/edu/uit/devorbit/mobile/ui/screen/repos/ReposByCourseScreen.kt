package vn.edu.uit.devorbit.mobile.ui.screen.repos

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.ui.RepoDetailScreen
import vn.edu.uit.devorbit.mobile.ui.RepoFilterState
import vn.edu.uit.devorbit.mobile.ui.RepoListSection
import vn.edu.uit.devorbit.mobile.ui.screen.courses.CourseSearchFilterState
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.CourseViewModel

object ReposCourseCatalog {
    fun visibleCourses(courses: List<CourseEntity>): List<CourseEntity> {
        return courses
            .filter { it.repoCount > 0 }
            .sortedWith(compareByDescending<CourseEntity> { it.repoCount }.thenBy { it.maMH })
    }
}

data class ReposCourseRowModel(
    val code: String,
    val name: String,
    val meta: String
) {
    companion object {
        fun fromCourse(course: CourseEntity): ReposCourseRowModel {
            val parts = buildList {
                if (course.repoCount > 0) add("${course.repoCount} repo")
                if (course.credits > 0) add("${course.credits} TC")
            }
            return ReposCourseRowModel(
                code = course.maMH,
                name = course.tenMH,
                meta = parts.joinToString(" · ")
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReposByCourseScreen(
    viewModel: CourseViewModel = hiltViewModel()
) {
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val filterState by viewModel.courseSearchFilterState.collectAsStateWithLifecycle()
    val selectedCourse by viewModel.selectedCourse.collectAsStateWithLifecycle()
    val selectedRepo by viewModel.selectedRepo.collectAsStateWithLifecycle()
    val repos by viewModel.detailRepos.collectAsStateWithLifecycle()
    val detailLoading by viewModel.detailLoading.collectAsStateWithLifecycle()
    val detailError by viewModel.detailError.collectAsStateWithLifecycle()
    val visibleCourses = remember(courses) { ReposCourseCatalog.visibleCourses(courses) }

    when {
        selectedRepo != null -> RepoDetailScreen(
            repo = selectedRepo!!,
            onBack = { viewModel.backFromRepo() }
        )

        selectedCourse != null -> RepoCourseDetail(
            course = selectedCourse!!,
            repos = repos,
            loading = detailLoading,
            error = detailError,
            onBack = { viewModel.closeCourseDetail() },
            onRepoClick = { viewModel.openRepo(it) }
        )

        else -> Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Môn học",
                style = CosmicTheme.typography.display,
                color = CosmicTheme.colors.textPrimary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)
            )
            Text(
                text = "Chọn môn học để xem repo liên quan",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

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

            if (visibleCourses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không tìm thấy môn học có repo phù hợp",
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
                items(visibleCourses, key = { it.id }) { course ->
                    ReposCourseRow(
                        model = ReposCourseRowModel.fromCourse(course),
                        onClick = { viewModel.openCourse(course) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReposCourseRow(
    model: ReposCourseRowModel,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = model.code,
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.plasma
                )
                Text(
                    text = model.name,
                    style = CosmicTheme.typography.body,
                    color = CosmicTheme.colors.textPrimary,
                    maxLines = 2
                )
            }
            if (model.meta.isNotBlank()) {
                Text(
                    text = model.meta,
                    style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.SemiBold),
                    color = CosmicTheme.colors.textTertiary
                )
            }
        }
    }
}

@Composable
private fun RepoCourseDetail(
    course: CourseEntity,
    repos: List<vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary>,
    loading: Boolean,
    error: String?,
    onBack: () -> Unit,
    onRepoClick: (vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary) -> Unit
) {
    var selectedTechStack by remember(repos) { mutableStateOf<String?>(null) }
    val repoFilterState = remember(repos, selectedTechStack) {
        RepoFilterState(repos = repos, selectedTechStack = selectedTechStack)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
            Text("< Môn học", color = CosmicTheme.colors.textSecondary)
        }
        Text(
            text = course.tenMH,
            style = CosmicTheme.typography.display,
            color = CosmicTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = course.maMH,
            style = CosmicTheme.typography.label,
            color = CosmicTheme.colors.plasma,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CosmicTheme.colors.plasma, strokeWidth = 2.dp)
            }
            error != null -> Text(
                text = error,
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.supernova,
                modifier = Modifier.padding(16.dp)
            )
            repos.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Môn học này chưa có repo",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textTertiary
                )
            }
            else -> RepoListSection(
                repos = repoFilterState.filteredRepos,
                resultCount = repoFilterState.filteredRepos.size,
                totalCount = repos.size,
                availableTechStacks = repoFilterState.availableTechStacks,
                selectedTechStack = repoFilterState.selectedTechStack,
                onTechStackSelected = { stack ->
                    selectedTechStack = repoFilterState.selectTechStack(stack).selectedTechStack
                },
                onRepoClick = onRepoClick
            )
        }
    }
}
