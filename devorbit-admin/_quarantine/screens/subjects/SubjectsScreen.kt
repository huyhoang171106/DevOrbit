package vn.edu.uit.devorbit.admin.screens.subjects

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.admin.components.EmptyStateScene
import vn.edu.uit.devorbit.admin.components.LoadingStructure
import vn.edu.uit.devorbit.admin.components.SubjectProgressNode
import vn.edu.uit.devorbit.admin.data.Subject
import vn.edu.uit.devorbit.admin.data.SubjectStatus
import vn.edu.uit.devorbit.admin.design.OrbitColors
import vn.edu.uit.devorbit.admin.design.OrbitShapes
import vn.edu.uit.devorbit.admin.design.OrbitSpacing

// ── Filter / Sort Types ───────────────────────────────────────────────────────

private enum class SubjectFilter(val label: String) {
    All("All"),
    Active("Active"),
    Bookmarked("Bookmarked"),
    Completed("Completed"),
}

private enum class SubjectSort(val label: String) {
    Name("Name"),
    Code("Code"),
    Progress("Progress"),
}

// ── Main Screen ──────────────────────────────────────────────────────────────

/**
 * Subjects list screen with collapsing title + search bar, filter chips,
 * sort control, and a subject list where the first active subject uses
 * the featured [SubjectProgressNode] variant.
 *
 * States: Loading, Content, Empty filtered result, Error.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubjectsScreen(
    modifier: Modifier = Modifier,
    onSubjectClick: (subjectId: String) -> Unit = {},
) {
    val spacing = OrbitSpacing

    // ── UI state ──────────────────────────────────────────────────────────
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(SubjectFilter.All) }
    var sortOrder by remember { mutableStateOf(SubjectSort.Name) }
    var showSortMenu by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()

    // ── Sample data ───────────────────────────────────────────────────────
    val allSubjects = rememberSampleSubjects()

    // Filter + search + sort pipeline
    val filteredSubjects = remember(allSubjects, selectedFilter, searchQuery, sortOrder) {
        allSubjects
            .filter { subject ->
                when (selectedFilter) {
                    SubjectFilter.All -> true
                    SubjectFilter.Active -> subject.status == SubjectStatus.Active
                    SubjectFilter.Bookmarked -> subject.isBookmarked
                    SubjectFilter.Completed -> subject.status == SubjectStatus.Completed
                }
            }
            .filter { subject ->
                searchQuery.isBlank() ||
                    subject.title.contains(searchQuery, ignoreCase = true) ||
                    subject.code.contains(searchQuery, ignoreCase = true)
            }
            .sortedWith(
                when (sortOrder) {
                    SubjectSort.Name -> compareBy { it.title.lowercase() }
                    SubjectSort.Code -> compareBy { it.code.lowercase() }
                    SubjectSort.Progress -> compareByDescending { it.progress }
                }
            )
    }

    // Index of the first active subject in the filtered list — that one
    // renders as featured; all others (including other active subjects) compact.
    val firstActiveIndex = remember(filteredSubjects) {
        filteredSubjects.indexOfFirst { it.status == SubjectStatus.Active }
    }

    // ── Collapsing header driven by scroll ────────────────────────────────
    val collapseProgress by remember {
        derivedStateOf {
            val offset = listState.firstVisibleItemScrollOffset
            (offset / 160f).coerceIn(0f, 1f)
        }
    }

    val headerAlpha by animateFloatAsState(
        targetValue = 1f - collapseProgress,
        animationSpec = tween(250), label = "headerAlpha",
    )
    val searchVisibleAlpha by remember(collapseProgress) {
        derivedStateOf {
            if (collapseProgress < 0.5f) 1f else (1f - (collapseProgress - 0.5f) / 0.5f)
        }
    }

    // ── Layout ────────────────────────────────────────────────────────────
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrbitColors.BackgroundDeep),
    ) {
        // ── Collapsing header (outside scroll) ────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = spacing.screenHorizontal,
                    end = spacing.screenHorizontal,
                    top = spacing.standard,
                ),
        ) {
            Text(
                text = "Subjects",
                style = MaterialTheme.typography.headlineLarge,
                color = OrbitColors.TextPrimary.copy(alpha = headerAlpha),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(8.dp))

            // Search bar fades out as header collapses
            SubjectsSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(searchVisibleAlpha),
            )
        }

        // ── Scrollable content ────────────────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            // Sticky filter + sort row
            stickyHeader {
                FilterAndSortBar(
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it },
                    sortOrder = sortOrder,
                    showSortMenu = showSortMenu,
                    onShowSortMenu = { showSortMenu = it },
                    onSortOrderChange = { sortOrder = it },
                )
            }

            // ── Content by state ──────────────────────────────────────────
            when {
                isLoading -> {
                    item(key = "loading") {
                        LoadingStructure(
                            modifier = Modifier.padding(
                                horizontal = spacing.screenHorizontal,
                                vertical = spacing.section,
                            ),
                            lines = 5,
                        )
                    }
                }

                errorMessage != null -> {
                    item(key = "error") {
                        ErrorState(
                            message = errorMessage!!,
                            onRetry = { errorMessage = null },
                        )
                    }
                }

                filteredSubjects.isEmpty() -> {
                    item(key = "empty") {
                        EmptyStateScene(
                            title = if (searchQuery.isNotBlank() || selectedFilter != SubjectFilter.All)
                                "No matching subjects"
                            else
                                "No subjects yet",
                            message = if (searchQuery.isNotBlank())
                                "Try a different search term or filter."
                            else
                                "Your subjects will appear here once added.",
                            modifier = Modifier.padding(
                                top = spacing.major,
                                bottom = spacing.major,
                            ),
                        )
                    }
                }

                else -> {
                    itemsIndexed(
                        items = filteredSubjects,
                        key = { _, s -> s.id },
                    ) { index, subject ->
                        SubjectProgressNode(
                            title = subject.title,
                            code = subject.code,
                            progress = subject.progress,
                            status = subject.status.name.lowercase(),
                            color = subject.color,
                            nextTask = subject.nextTask,
                            estimatedTime = subject.estimatedTime,
                            onClick = { onSubjectClick(subject.id) },
                            featured = index == firstActiveIndex,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = spacing.screenHorizontal)
                                .padding(bottom = spacing.listItemGap),
                        )
                    }

                    // Bottom spacer so last item isn't hidden behind dock
                    item(key = "spacer") {
                        Spacer(Modifier.height(96.dp))
                    }
                }
            }
        }
    }
}

// ── Search Bar ────────────────────────────────────────────────────────────────

@Composable
private fun SubjectsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitSpacing

    Row(
        modifier = modifier
            .clip(OrbitShapes.compactControl)
            .background(OrbitColors.SurfaceRaised)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = "Search",
            tint = OrbitColors.TextMuted,
            modifier = Modifier.size(18.dp),
        )

        Spacer(Modifier.width(spacing.compact))

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = OrbitColors.TextPrimary,
                fontFamily = FontFamily.Default,
            ),
            cursorBrush = SolidColor(OrbitColors.PrimaryElectricBlue),
            decorationBox = { innerTextField ->
                Box {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search subjects\u2026",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OrbitColors.TextMuted,
                        )
                    }
                    innerTextField()
                }
            },
        )

        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Clear",
                    tint = OrbitColors.TextMuted,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

// ── Filter + Sort Bar (sticky) ───────────────────────────────────────────────

@Composable
private fun FilterAndSortBar(
    selectedFilter: SubjectFilter,
    onFilterChange: (SubjectFilter) -> Unit,
    sortOrder: SubjectSort,
    showSortMenu: Boolean,
    onShowSortMenu: (Boolean) -> Unit,
    onSortOrderChange: (SubjectSort) -> Unit,
) {
    val spacing = OrbitSpacing

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OrbitColors.BackgroundDeep)
            .padding(top = spacing.compact, bottom = spacing.compact),
    ) {
        // Filter chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = spacing.screenHorizontal),
            horizontalArrangement = Arrangement.spacedBy(spacing.compact),
            contentPadding = PaddingValues(end = spacing.screenHorizontal),
        ) {
            itemsIndexed(
                items = SubjectFilter.entries,
                key = { _, f -> f.name },
            ) { _, filter ->
                FilterChipView(
                    label = filter.label,
                    selected = filter == selectedFilter,
                    onClick = { onFilterChange(filter) },
                )
            }
        }

        Spacer(Modifier.height(spacing.compact))

        // Sort control — right-aligned
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.screenHorizontal),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.weight(1f))

            Box {
                Row(
                    modifier = Modifier
                        .clip(OrbitShapes.compactControl)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onShowSortMenu(!showSortMenu) },
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SortByAlpha,
                        contentDescription = "Sort",
                        tint = OrbitColors.TextSecondary,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = sortOrder.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = OrbitColors.TextSecondary,
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { onShowSortMenu(false) },
                    containerColor = OrbitColors.SurfaceRaised,
                    shape = OrbitShapes.contentPanel,
                ) {
                    SubjectSort.entries.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (option == sortOrder)
                                        OrbitColors.PrimaryElectricBlue
                                    else
                                        OrbitColors.TextPrimary,
                                )
                            },
                            onClick = {
                                onSortOrderChange(option)
                                onShowSortMenu(false)
                            },
                        )
                    }
                }
            }
        }
    }
}

// ── Filter Chip ───────────────────────────────────────────────────────────────

@Composable
private fun FilterChipView(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (selected) OrbitColors.PrimaryElectricBlue.copy(alpha = 0.15f)
    else OrbitColors.SurfaceRaised
    val textColor = if (selected) OrbitColors.PrimaryElectricBlue
    else OrbitColors.TextSecondary

    Box(
        modifier = modifier
            .clip(OrbitShapes.compactControl)
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
        )
    }
}

// ── Error State ───────────────────────────────────────────────────────────────

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitSpacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenHorizontal, vertical = spacing.hero),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleSmall,
            color = OrbitColors.Error,
        )
        Spacer(Modifier.height(spacing.compact))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = OrbitColors.TextSecondary,
        )
        Spacer(Modifier.height(spacing.standard))

        Box(
            modifier = Modifier
                .clip(OrbitShapes.primaryAction)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onRetry,
                )
                .background(OrbitColors.PrimaryElectricBlue.copy(alpha = 0.15f))
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Text(
                text = "Retry",
                style = MaterialTheme.typography.labelLarge,
                color = OrbitColors.PrimaryElectricBlue,
            )
        }
    }
}

// ── Sample Data (preview / development) ───────────────────────────────────────

@Composable
private fun rememberSampleSubjects(): List<Subject> {
    return remember {
        listOf(
            Subject(
                id = "cs201",
                code = "CS201",
                title = "Data Structures & Algorithms",
                description = "Core DSA with practical applications",
                credits = 4,
                difficulty = "Advanced",
                progress = 0.72f,
                status = SubjectStatus.Active,
                color = OrbitColors.ChartBlue,
                nextTask = "AVL Tree Rotations",
                estimatedTime = "45 min",
                semester = "Fall 2025",
                isBookmarked = true,
            ),
            Subject(
                id = "cs205",
                code = "CS205",
                title = "Operating Systems",
                description = "Process management, memory, file systems",
                credits = 4,
                difficulty = "Advanced",
                progress = 0.35f,
                status = SubjectStatus.Active,
                color = OrbitColors.ChartCyan,
                nextTask = "Page Replacement Algorithms",
                estimatedTime = "1h 10min",
                semester = "Fall 2025",
                isBookmarked = false,
            ),
            Subject(
                id = "ma101",
                code = "MA101",
                title = "Calculus I",
                description = "Limits, derivatives, integrals",
                credits = 3,
                difficulty = "Intermediate",
                progress = 0.91f,
                status = SubjectStatus.Active,
                color = OrbitColors.ChartGreen,
                nextTask = "Integration by Parts Quiz",
                estimatedTime = "30 min",
                semester = "Fall 2025",
                isBookmarked = true,
            ),
            Subject(
                id = "cs301",
                code = "CS301",
                title = "Computer Networks",
                description = "TCP/IP, routing, application layer",
                credits = 3,
                difficulty = "Intermediate",
                progress = 0.0f,
                status = SubjectStatus.Locked,
                color = OrbitColors.ChartYellow,
                nextTask = "",
                estimatedTime = "",
                semester = "Spring 2026",
                isBookmarked = false,
            ),
            Subject(
                id = "cs401",
                code = "CS401",
                title = "Machine Learning",
                description = "Supervised, unsupervised, neural networks",
                credits = 4,
                difficulty = "Advanced",
                progress = 1.0f,
                status = SubjectStatus.Completed,
                color = OrbitColors.ChartOrange,
                nextTask = "",
                estimatedTime = "",
                semester = "Spring 2025",
                isBookmarked = false,
            ),
            Subject(
                id = "en201",
                code = "EN201",
                title = "Technical Writing",
                description = "Documentation, reports, proposals",
                credits = 2,
                difficulty = "Beginner",
                progress = 1.0f,
                status = SubjectStatus.Completed,
                color = OrbitColors.ChartRed,
                nextTask = "",
                estimatedTime = "",
                semester = "Fall 2024",
                isBookmarked = false,
            ),
            Subject(
                id = "ma201",
                code = "MA201",
                title = "Linear Algebra",
                description = "Vectors, matrices, eigenvalues",
                credits = 3,
                difficulty = "Intermediate",
                progress = 0.55f,
                status = SubjectStatus.Paused,
                color = OrbitColors.ChartMuted,
                nextTask = "Eigen-decomposition",
                estimatedTime = "50 min",
                semester = "Fall 2025",
                isBookmarked = true,
            ),
        )
    }
}
