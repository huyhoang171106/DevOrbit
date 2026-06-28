package vn.edu.uit.devorbit.mobile.ui.screen.knowledge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.domain.model.GraphNode
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.CourseViewModel

@Composable
fun KnowledgeTabView(courseViewModel: CourseViewModel = hiltViewModel()) {
    val nodes by courseViewModel.graphNodes.collectAsState()
    val links by courseViewModel.graphLinks.collectAsState()
    val loading by courseViewModel.graphLoading.collectAsState()

    var selectedNode by remember { mutableStateOf<GraphNode?>(null) }

    LaunchedEffect(Unit) { courseViewModel.loadGraph() }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = CosmicTheme.colors.plasma,
                strokeWidth = 2.dp
            )
        }
    } else {
        KnowledgeGraphScreen(
            nodes = nodes,
            links = links,
            learningPath = emptyList(),
            selectedNode = selectedNode,
            onNodeClick = { selectedNode = it }
        )
    }
}
