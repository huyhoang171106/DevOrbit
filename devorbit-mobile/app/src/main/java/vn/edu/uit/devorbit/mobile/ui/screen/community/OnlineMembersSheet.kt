package vn.edu.uit.devorbit.mobile.ui.screen.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.OnlineMember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineMembersSheet(
    members: List<OnlineMember>,
    connected: Boolean,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CosmicTheme.colors.nebula,
        contentColor = CosmicTheme.colors.textPrimary,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = "Đang hoạt động",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicTheme.colors.textPrimary
                )
                if (connected && members.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${members.size} online",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicTheme.colors.plasma
                        )
                    }
                }
            }

            if (!connected) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = CosmicTheme.colors.plasma,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else if (members.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Chưa có ai online",
                        fontSize = 13.sp,
                        color = CosmicTheme.colors.textTertiary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(members, key = { it.studentCode }) { member ->
                        OnlineMemberItem(member = member)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OnlineMemberItem(member: OnlineMember) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    CosmicTheme.colors.plasma.copy(alpha = 0.2f),
                    RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = member.displayName.take(1).uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CosmicTheme.colors.plasma
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = member.displayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = CosmicTheme.colors.textPrimary
            )
            Text(
                text = member.studentCode,
                fontSize = 11.sp,
                color = CosmicTheme.colors.textTertiary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    CosmicTheme.colors.plasma,
                    RoundedCornerShape(4.dp)
                )
        )
    }
}
