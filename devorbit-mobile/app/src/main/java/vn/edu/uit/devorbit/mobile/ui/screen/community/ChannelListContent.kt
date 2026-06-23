package vn.edu.uit.devorbit.mobile.ui.screen.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.ChatChannel

private val CHANNEL_GROUP_LABELS = mapOf(
    "GENERAL" to "Chung",
    "COURSE" to "Môn học",
    "TECH_STACK" to "Tech Stack"
)

@Composable
fun ChannelListContent(
    channels: List<ChatChannel>,
    activeChannel: ChatChannel?,
    onSelect: (ChatChannel) -> Unit,
    modifier: Modifier = Modifier
) {
    var search by remember { mutableStateOf("") }
    val isSearching = search.isNotBlank()

    val filtered = if (isSearching) {
        channels.filter { it.name.contains(search, ignoreCase = true) }
    } else channels

    val grouped = filtered.groupBy { it.type }
    val typeOrder = listOf("GENERAL", "COURSE", "TECH_STACK")

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Kênh",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = CosmicTheme.colors.textPrimary,
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Tìm kênh...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CosmicTheme.colors.plasma,
                cursorColor = CosmicTheme.colors.plasma
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            typeOrder.forEach { type ->
                val list = grouped[type]
                if (!list.isNullOrEmpty()) {
                    item(key = "header-$type") {
                        val groupColor = when (type) {
                            "GENERAL" -> CosmicTheme.colors.plasma
                            "COURSE" -> CosmicTheme.colors.textPrimary
                            "TECH_STACK" -> CosmicTheme.colors.textSecondary
                            else -> CosmicTheme.colors.textTertiary
                        }
                        Text(
                            text = CHANNEL_GROUP_LABELS[type] ?: type,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = groupColor,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(list, key = { it.id }) { channel ->
                        ChannelItem(
                            channel = channel,
                            isActive = channel.id == activeChannel?.id,
                            onClick = { onSelect(channel) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelItem(
    channel: ChatChannel,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isActive) CosmicTheme.colors.plasma.copy(alpha = 0.1f)
                else androidx.compose.material3.MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) CosmicTheme.colors.plasma else CosmicTheme.colors.textTertiary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = channel.name,
            fontSize = 14.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) CosmicTheme.colors.plasma else CosmicTheme.colors.textPrimary
        )
    }
}
