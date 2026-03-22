package uk.co.deftelf.gorest.ui.screen

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.compose.NavigationBackHandler
import gorest.presentation.generated.resources.Res
import gorest.presentation.generated.resources.add_user_fab_description
import gorest.presentation.generated.resources.no_internet_connection
import gorest.presentation.generated.resources.this_user
import gorest.presentation.generated.resources.undo
import gorest.presentation.generated.resources.user_deleted
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import uk.co.deftelf.gorest.data.connectivity.NetworkMonitor
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedEffect
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedUiEvent
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
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var fabVisible by remember { mutableStateOf(true) }
    var prevIndex by remember { mutableStateOf(0) }
    var prevOffset by remember { mutableStateOf(0) }
    val scrollThresholdPx = with(LocalDensity.current) { 50.dp.toPx() }.toInt()

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                if (index != prevIndex || kotlin.math.abs(offset - prevOffset) > scrollThresholdPx) {
                    fabVisible = index < prevIndex || (index == prevIndex && offset <= prevOffset)
                    prevIndex = index
                    prevOffset = offset
                }
            }
    }

    LaunchedEffect(networkMonitor.isConnected) {
        var noNetworkJob: Job? = null
        networkMonitor.isConnected.collect { connected ->
            noNetworkJob?.cancel()
            noNetworkJob = if (connected) {
                null
            } else {
                launch {
                    snackbarHostState.showSnackbar(
                        message = getString(Res.string.no_internet_connection),
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
                        message = getString(Res.string.user_deleted, effect.userName),
                        actionLabel = getString(Res.string.undo),
                        duration = SnackbarDuration.Long,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.processIntent(UserFeedUiEvent.UndoDelete(effect.userId))
                    }
                }
                is UserFeedEffect.ShowError -> {
                    snackbarHostState.showSnackbar(message = effect.message)
                }
            }
        }
    }

    val fabTranslationY by animateFloatAsState(
        targetValue = if (fabVisible) 0f else with(LocalDensity.current) { 100.dp.toPx() },
        animationSpec = if (fabVisible)
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        else
            tween(durationMillis = 200, easing = FastOutLinearInEasing),
        label = "fabTranslationY",
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                modifier = Modifier.graphicsLayer { translationY = fabTranslationY },
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.add_user_fab_description))
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
            LazyColumn(state = listState, contentPadding = paddingValues) {
                items(
                    items = state.users,
                    key = { it.id },
                ) { user ->
                    UserCard(
                        user = user,
                        onClick = { onNavigateToDetail(user.id) },
                        onLongClick = { viewModel.processIntent(UserFeedUiEvent.RequestDelete(user.id)) },
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
                userName = user?.name ?: stringResource(Res.string.this_user),
                onConfirm = { viewModel.processIntent(UserFeedUiEvent.ConfirmDelete(userId)) },
                onDismiss = { viewModel.processIntent(UserFeedUiEvent.DismissError) },
            )
        }
    }
}
