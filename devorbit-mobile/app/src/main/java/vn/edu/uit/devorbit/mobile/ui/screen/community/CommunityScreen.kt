package vn.edu.uit.devorbit.mobile.ui.screen.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var showMembers by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadChannels()
        viewModel.connect()
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    if (uiState.isLoadingChannels && uiState.channels.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = CosmicTheme.colors.plasma,
                strokeWidth = 2.dp
            )
        }
        return
    }

    if (uiState.channels.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Cộng đồng",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.error ?: "Đăng nhập để tham gia",
                    fontSize = 14.sp,
                    color = CosmicTheme.colors.textTertiary
                )
            }
        }
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CosmicTheme.colors.nebula
            ) {
                ChannelListContent(
                    channels = uiState.channels,
                    activeChannel = uiState.activeChannel,
                    onSelect = { channel ->
                        viewModel.selectChannel(channel)
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = uiState.activeChannel?.name ?: "Cộng đồng",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTheme.colors.textPrimary
                            )
                            if (uiState.activeChannel != null) {
                                Text(
                                    text = when (uiState.activeChannel!!.type) {
                                        "GENERAL" -> "Chung"
                                        "COURSE" -> "Môn học"
                                        "TECH_STACK" -> "Tech Stack"
                                        else -> ""
                                    },
                                    fontSize = 11.sp,
                                    color = CosmicTheme.colors.textTertiary
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Mở danh sách kênh")
                        }
                    },
                    actions = {
                        if (uiState.onlineMembers.isNotEmpty()) {
                            BadgedBox(
                                badge = {
                                    Badge {
                                        Text("${uiState.onlineMembers.size}")
                                    }
                                }
                            ) {
                                IconButton(onClick = { showMembers = true }) {
                                    Icon(
                                        Icons.Default.People,
                                        contentDescription = "Thành viên online"
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    )
                )
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (uiState.isLoadingMessages && uiState.messages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = CosmicTheme.colors.plasma,
                            strokeWidth = 2.dp
                        )
                    }
                } else if (uiState.messages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chưa có tin nhắn nào",
                            fontSize = 14.sp,
                            color = CosmicTheme.colors.textTertiary
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            uiState.messages,
                            key = { if (it.id > 0) it.id else "temp-${it.id}" }
                        ) { msg ->
                            val isMine = msg.studentId == uiState.currentUserId
                            val showSender = true
                            ChatMessageBubble(
                                message = msg,
                                isMine = isMine,
                                showSender = showSender
                            )
                        }
                    }
                }

                ChatInputBar(
                    channelName = uiState.activeChannel?.name ?: "",
                    onSend = { viewModel.sendMessage(it) },
                    onSendImage = { uri -> viewModel.uploadAndSendImage(uri, context) },
                    enabled = uiState.isConnected && uiState.activeChannel != null
                )
            }
        }
    }

    OnlineMembersSheet(
        members = uiState.onlineMembers,
        connected = uiState.isConnected,
        isVisible = showMembers,
        onDismiss = { showMembers = false }
    )
}
