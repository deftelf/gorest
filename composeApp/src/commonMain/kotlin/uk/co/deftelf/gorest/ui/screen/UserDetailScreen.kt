package uk.co.deftelf.gorest.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedViewModel
import uk.co.deftelf.gorest.ui.component.UserDetailPanel

@Composable
fun UserDetailScreen(
    userId: Long?,
    viewModel: UserFeedViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val user = state.users.find { it.id == userId }
    UserDetailPanel(user = user, modifier = Modifier.fillMaxSize().safeDrawingPadding())
}
