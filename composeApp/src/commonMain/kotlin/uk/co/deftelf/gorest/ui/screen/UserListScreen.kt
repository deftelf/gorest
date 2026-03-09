package uk.co.deftelf.gorest.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.navigationevent.compose.NavigationBackHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import uk.co.deftelf.gorest.data.connectivity.NetworkMonitor
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedEffect
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedIntent
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedViewModel
import uk.co.deftelf.gorest.ui.component.DeleteConfirmDialog
import uk.co.deftelf.gorest.ui.component.ShimmerHost
import uk.co.deftelf.gorest.ui.component.UserCard
import uk.co.deftelf.gorest.ui.component.UserCardShimmer

@Composable
fun UserListScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: UserFeedViewModel = koinViewModel(),
    networkMonitor: NetworkMonitor = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(networkMonitor.isConnected) {
        var noNetworkJob: Job? = null
        networkMonitor.isConnected.collect { connected ->
            noNetworkJob?.cancel()
            noNetworkJob = if (connected) {
                null
            } else {
                launch {
                    snackbarHostState.showSnackbar(
                        message = "No internet connection",
                        duration = SnackbarDuration.Indefinite,
                    )
                }
            }
        }
    }

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
        if (state.isLoading && state.users.isEmpty()) {
            ShimmerHost {
                LazyColumn(contentPadding = paddingValues) {
                    items(100) {
                        UserCardShimmer(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        )
                    }
                }
            }
        } else {
            LazyColumn(contentPadding = paddingValues) {
                items(
                    items = state.users,
                    key = { it.id },
                ) { user ->
                    UserCard(
                        user = user,
                        onClick = { onNavigateToDetail(user.id) },
                        onLongClick = { viewModel.processIntent(UserFeedIntent.RequestDelete(user.id)) },
                        modifier = Modifier
                            .animateItem()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                    )
                }
            }
        }

        state.pendingDeleteId?.let { userId ->
            val user = state.users.find { it.id == userId }
            DeleteConfirmDialog(
                userId = userId,
                userName = user?.name ?: "this user",
                onConfirm = { viewModel.processIntent(UserFeedIntent.ConfirmDelete(userId)) },
                onDismiss = { viewModel.processIntent(UserFeedIntent.DismissError) },
            )
        }
    }
}
