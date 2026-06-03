package vn.edu.uit.devorbit.mobile.ui.screen.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.SubjectQaMessage
import vn.edu.uit.devorbit.mobile.ui.viewmodel.SubjectQaViewModel

@Composable
fun SubjectQaScreen(
    viewModel: SubjectQaViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Hoi AI ve mon hoc",
            style = CosmicTheme.typography.display,
            color = CosmicTheme.colors.textPrimary
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.messages) { message ->
                SubjectQaMessageCard(message)
            }
        }

        state.error?.let {
            Text(it, style = CosmicTheme.typography.label, color = CosmicTheme.colors.supernova)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Nhap cau hoi...") },
                singleLine = true
            )
            Button(
                enabled = input.isNotBlank() && !state.loading,
                onClick = {
                    viewModel.ask(input)
                    input = ""
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CosmicTheme.colors.plasma,
                    contentColor = CosmicTheme.colors.void
                )
            ) {
                Text(if (state.loading) "..." else "Gui")
            }
        }
    }
}

@Composable
private fun SubjectQaMessageCard(message: SubjectQaMessage) {
    val isUser = message.role == "user"
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isUser) CosmicTheme.colors.plasma.copy(alpha = 0.12f) else CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = if (isUser) "Ban" else "AI",
                style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                color = if (isUser) CosmicTheme.colors.plasma else CosmicTheme.colors.aurora
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = message.text,
                style = CosmicTheme.typography.body,
                color = CosmicTheme.colors.textPrimary
            )
            if (message.sources.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = message.sources.joinToString(" | "),
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textTertiary
                )
            }
        }
    }
}
