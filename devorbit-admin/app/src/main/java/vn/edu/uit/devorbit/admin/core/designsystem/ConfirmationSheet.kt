package vn.edu.uit.devorbit.admin.core.designsystem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationSheet(
    visible: Boolean,
    title: String,
    message: String,
    confirmLabel: String,
    confirmDanger: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    loading: Boolean = false,
) {
    if (!visible) return

    ModalBottomSheet(
        onDismissRequest = { if (!loading) onDismiss() },
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = Surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Divider) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (confirmDanger) {
                Icon(
                    Icons.Rounded.WarningAmber,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Danger,
                )
                Spacer(Modifier.height(12.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AdminSecondaryButton(
                    text = "Huỷ",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    enabled = !loading,
                )
                if (confirmDanger) {
                    AdminDangerButton(
                        text = confirmLabel,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        loading = loading,
                    )
                } else {
                    AdminPrimaryButton(
                        text = confirmLabel,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        loading = loading,
                    )
                }
            }
        }
    }
}
