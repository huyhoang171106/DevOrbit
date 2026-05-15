package vn.edu.uit.devorbit.mobile.ui.screen.knowledge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.KnowledgeViewModel

@Composable
fun KnowledgeHubScreen(
    viewModel: KnowledgeViewModel = hiltViewModel(),
    onNavigateToCourse: ((String) -> Unit)? = null
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadGraph() }

    if (state.loading && state.nodes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CosmicTheme.colors.plasma)
        }
        return
    }

    if (state.detailNode != null) {
        val detailNode = state.detailNode!!
        KnowledgeDetailScreen(
            node = detailNode,
            learningPath = state.learningPath,
            onSimulate = { viewModel.simulateFail(detailNode.id) },
            onBack = { viewModel.closeDetail() },
            onViewResources = { code ->
                onNavigateToCourse?.invoke(code)
                viewModel.closeDetail()
            },
            onToggleComplete = { viewModel.toggleComplete(it) },
            isCompleted = detailNode.id in state.completedNodeIds,
            simulationMode = state.simulationMode,
            simulationFailedIds = state.simulationFailedNodeIds
        )
    } else {
        KnowledgeGraphScreen(
            nodes = state.getFilteredNodes(),
            links = state.links,
            learningPath = state.learningPath,
            selectedNode = state.selectedNode,
            onNodeClick = { viewModel.selectNode(it) },
            onInfoClick = { viewModel.openDetail(it) },
            searchQuery = state.searchQuery,
            onSearchQueryChange = { viewModel.search(it) },
            selectedSemester = state.selectedSemester,
            onSemesterFilterChange = { viewModel.setSemesterFilter(it) },
            completedNodeIds = state.completedNodeIds,
            onToggleComplete = { viewModel.toggleComplete(it) },
            simulationMode = state.simulationMode,
            simulationFailedIds = state.simulationFailedNodeIds,
            onToggleSimulation = { viewModel.toggleSimulationMode() },
            onClearSelection = { viewModel.clearSelection() },
            totalNodeCount = state.nodes.size
        )
    }
}
