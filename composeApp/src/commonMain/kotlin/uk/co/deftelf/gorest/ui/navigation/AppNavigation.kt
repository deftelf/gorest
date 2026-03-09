package uk.co.deftelf.gorest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import uk.co.deftelf.gorest.ui.screen.AddUserScreen
import uk.co.deftelf.gorest.ui.screen.UserDetailScreen
import uk.co.deftelf.gorest.ui.screen.UserListScreen

private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Destination.UserList::class)
            subclass(Destination.AddUser::class)
            subclass(Destination.UserDetail::class)
        }
    }
}

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(navConfig, Destination.UserList)

//    val sceneStrategy = remember { TwoPaneSceneStrategy<NavKey>().then(SinglePaneSceneStrategy()) }

    NavigationBackHandler(state = rememberNavigationEventState(NavigationEventInfo.None), isBackEnabled = backStack.size > 1) {
        backStack.removeLastOrNull()
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
//        sceneStrategy = sceneStrategy,
        entryProvider = entryProvider {
            entry<Destination.UserList> {
                UserListScreen(
                    onNavigateToAdd = { backStack.add(Destination.AddUser) },
                    onNavigateToDetail = { userId -> backStack.add(Destination.UserDetail(userId)) },
                )
            }
            entry<Destination.AddUser> {
                AddUserScreen(
                    onNavigateBack = { backStack.removeLastOrNull() },
                )
            }
            entry<Destination.UserDetail> { dest ->
                UserDetailScreen(userId = dest.userId)
            }
        },
    )
}
