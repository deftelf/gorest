package uk.co.deftelf.gorest.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedEffect
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedIntent
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedViewModel
import uk.co.deftelf.gorest.ui.component.AdaptiveUserFeedLayout
import uk.co.deftelf.gorest.ui.component.DeleteConfirmDialog

@Composable
fun UserFeedScreen(
    onNavigateToAdd: () -> Unit,
    viewModel: UserFeedViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is UserFeedEffect.ShowUndoSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "${effect.userName} deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Long,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.processIntent(UserFeedIntent.UndoDelete(effect.userId))
                    }
                }
                is UserFeedEffect.ShowError -> {
                    snackbarHostState.showSnackbar(message = effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add user")
            }
        },
    ) { paddingValues ->
        AdaptiveUserFeedLayout(
            state = state,
            onUserClick = { userId ->
                viewModel.processIntent(UserFeedIntent.SelectUser(userId))
            },
            onUserLongClick = { userId ->
                viewModel.processIntent(UserFeedIntent.RequestDelete(userId))
            },
            onDeselectUser = {
                viewModel.processIntent(UserFeedIntent.SelectUser(null))
            },
            modifier = Modifier.padding(paddingValues),
        )

        state.pendingDeleteId?.let { userId ->
            val user = state.users.find { it.id == userId }
            DeleteConfirmDialog(
                userId = userId,
                userName = user?.name ?: "this user",
                onConfirm = {
                    viewModel.processIntent(UserFeedIntent.ConfirmDelete(userId))
                },
                onDismiss = {
                    viewModel.processIntent(UserFeedIntent.DismissError)
                },
            )
        }
    }
}
