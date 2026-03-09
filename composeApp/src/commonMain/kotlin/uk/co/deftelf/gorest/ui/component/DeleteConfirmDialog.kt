package uk.co.deftelf.gorest.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import gorest.composeapp.generated.resources.Res
import gorest.composeapp.generated.resources.cancel
import gorest.composeapp.generated.resources.delete
import gorest.composeapp.generated.resources.delete_confirm_message
import gorest.composeapp.generated.resources.delete_user_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteConfirmDialog(
    userId: Long,
    userName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.delete_user_title)) },
        text = { Text(stringResource(Res.string.delete_confirm_message, userName)) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(Res.string.delete)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) }
        },
    )
}
