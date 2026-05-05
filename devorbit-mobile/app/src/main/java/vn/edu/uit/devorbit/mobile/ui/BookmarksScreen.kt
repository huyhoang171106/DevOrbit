package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.model.BookmarkResponse
import vn.edu.uit.devorbit.mobile.repository.DevOrbitRepository

@Composable
fun BookmarksScreen(repository: DevOrbitRepository, onBack: () -> Unit) {
    var bookmarks by remember { mutableStateOf<List<BookmarkResponse>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(repository) {
        repository.getBookmarks()
            .onSuccess { bookmarks = it }
            .onFailure { error = it.message ?: "Failed to load bookmarks" }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack) { Text("< Back") }
        Text("Bookmarks", style = MaterialTheme.typography.headlineSmall)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 16.dp)) {
            items(bookmarks) { bookmark ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(bookmark.title, style = MaterialTheme.typography.titleMedium)
                        bookmark.subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                        Text(bookmark.targetType.name, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
