package vn.edu.uit.devorbit.mobile.ui.screen.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.mobile.model.domain.GraphNode
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseHubScreen(
    viewModel: CourseViewModel = hiltViewModel()
) {
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CosmicTheme.spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "COURSE HUB",
                style = CosmicTheme.typography.command,
                color = Color.White
            )

            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = viewMode == ViewMode.LIST,
                    onClick = { viewMode = ViewMode.LIST },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    icon = { Icon(Icons.Rounded.List, contentDescription = null) }
                ) {
                    Text("List")
                }
                SegmentedButton(
                    selected = viewMode == ViewMode.GALAXY,
                    onClick = { viewMode = ViewMode.GALAXY },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    icon = { Icon(Icons.Rounded.Star, contentDescription = null) }
                ) {
                    Text("Graph")
                }
            }
        }

        if (viewMode == ViewMode.LIST) {
            CourseListScreen(viewModel = viewModel, onCourseClick = {})
        } else {
            SemesterGraphView(viewModel = viewModel)
        }
    }
}

@Composable
private fun SemesterGraphView(viewModel: CourseViewModel) {
    val nodes by viewModel.graphNodes.collectAsState()
    val links by viewModel.graphLinks.collectAsState()
    val loading by viewModel.graphLoading.collectAsState()
    val error by viewModel.graphError.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadGraph()
    }

    when {
        loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CosmicTheme.colors.plasma)
        }
        error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $error", color = CosmicTheme.colors.supernova)
        }
        else -> {
            val bySemester = remember(nodes) {
                nodes.filter { it.semester != null && it.semester in 1..8 }
                    .groupBy { it.semester!! }.toSortedMap()
            }

            var expandedSemesters by remember { mutableStateOf(setOf<Int>()) }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = CosmicTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${nodes.size} courses · ${bySemester.size} semesters",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textSecondary
                    )
                    Spacer(Modifier.height(8.dp))
                }

                items(bySemester.entries.toList()) { (semester, semesterNodes) ->
                    SemesterCard(
                        semester = semester,
                        nodes = semesterNodes,
                        expanded = semester in expandedSemesters,
                        onToggle = {
                            expandedSemesters = if (semester in expandedSemesters)
                                expandedSemesters - semester
                            else expandedSemesters + semester
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SemesterCard(
    semester: Int,
    nodes: List<GraphNode>,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val bgColor = CosmicTheme.colors.glass
    val semesterColor = when (semester) {
        1, 2 -> CosmicTheme.colors.aurora
        3, 4 -> CosmicTheme.colors.plasma
        5, 6 -> Color(0xFFFF9F43)
        else -> CosmicTheme.colors.supernova
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(semesterColor)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "HK $semester",
                    style = CosmicTheme.typography.command,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${nodes.size} môn",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textSecondary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (expanded) "▲" else "▼",
                    color = CosmicTheme.colors.textSecondary,
                    fontSize = 10.sp
                )
            }
        }

        if (expanded) {
            nodes.forEach { node ->
                CourseNodeRow(node = node)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CourseNodeRow(node: GraphNode) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val dotColor = when {
            node.impactScore >= 7.0 -> CosmicTheme.colors.supernova
            node.impactScore >= 4.0 -> Color(0xFFFF9F43)
            else -> CosmicTheme.colors.aurora
        }
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(dotColor)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = node.code,
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.plasma
            )
            Text(
                text = node.name,
                style = CosmicTheme.typography.body,
                color = Color.White,
                maxLines = 1
            )
        }
        if (node.impactScore > 0) {
            Text(
                text = "%.1f".format(node.impactScore),
                style = CosmicTheme.typography.label,
                color = dotColor
            )
        }
    }
}

enum class ViewMode { LIST, GALAXY }
