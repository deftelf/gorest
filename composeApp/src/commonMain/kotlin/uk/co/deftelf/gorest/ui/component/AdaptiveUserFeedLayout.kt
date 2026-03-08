package uk.co.deftelf.gorest.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedState

@Composable
fun AdaptiveUserFeedLayout(
    state: UserFeedState,
    onUserClick: (Long) -> Unit,
    onUserLongClick: (Long) -> Unit,
    onDeselectUser: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 600.dp
        val selectedUser = state.users.find { it.id == state.selectedUserId }
        NavigationBackHandler(
            state = rememberNavigationEventState(NavigationEventInfo.None),
            isBackEnabled = selectedUser != null
        ) {
            onDeselectUser()
        }
        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                UserList(
                    state = state,
                    onUserClick = onUserClick,
                    onUserLongClick = onUserLongClick,
                    modifier = Modifier.weight(1f),
                )
                VerticalDivider(modifier = Modifier.fillMaxHeight())
                UserDetailPanel(
                    user = selectedUser,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            if (selectedUser != null) {
                UserDetailPanel(
                    user = selectedUser,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                UserList(
                    state = state,
                    onUserClick = onUserClick,
                    onUserLongClick = onUserLongClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun UserList(
    state: UserFeedState,
    onUserClick: (Long) -> Unit,
    onUserLongClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading && state.users.isEmpty()) {
        ShimmerHost {
            LazyColumn(modifier = modifier) {
                items(6) {
                    UserCardShimmer(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    )
                }
            }
        }
    } else {
        LazyColumn(modifier = modifier) {
            items(
                items = state.users,
                key = { it.id },
            ) { user ->
                UserCard(
                    user = user,
                    isSelected = user.id == state.selectedUserId,
                    onClick = { onUserClick(user.id) },
                    onLongClick = { onUserLongClick(user.id) },
                    modifier = Modifier
                        .animateItem()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }
        }
    }
}
