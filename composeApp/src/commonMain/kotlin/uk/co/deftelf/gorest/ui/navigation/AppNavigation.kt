package uk.co.deftelf.gorest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import uk.co.deftelf.gorest.ui.screen.AddUserScreen
import uk.co.deftelf.gorest.ui.screen.UserFeedScreen

@Composable
fun AppNavigation() {
    var currentDestination: Destination by remember { mutableStateOf(Destination.Feed) }

    when (currentDestination) {
        Destination.Feed -> UserFeedScreen(
            onNavigateToAdd = { currentDestination = Destination.AddUser },
        )
        Destination.AddUser -> AddUserScreen(
            onNavigateBack = { currentDestination = Destination.Feed },
        )
    }
}
