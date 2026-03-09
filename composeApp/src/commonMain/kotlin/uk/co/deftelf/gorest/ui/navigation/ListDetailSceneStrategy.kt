package uk.co.deftelf.gorest.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

/**
 * A [SceneStrategy] implementing a list-detail (two-pane) layout on wide screens.
 *
 * Returns null when [windowWidth] < [minWidthDp], deferring to the next strategy in the chain.
 * [destinations] must be the live NavBackStack so reads in [calculateScene] are always current.
 *
 * Behaviour (wide screens only):
 *  - `[UserList]`                      → UserList | [emptyDetailContent]
 *  - `[…, UserList, UserDetail(id)]`   → UserList | UserDetail screen
 *  - `[…, UserList, UserDetail(null)]` → UserList | UserDetail screen (shows empty-selection state)
 *  - Anything else                     → returns null
 */
class ListDetailSceneStrategy(
    private val windowWidth: Dp,
    private val destinations: List<NavKey>,
    private val minWidthDp: Dp = 600.dp,
    private val emptyDetailContent: @Composable () -> Unit,
) : SceneStrategy<NavKey> {

    override fun SceneStrategyScope<NavKey>.calculateScene(
        entries: List<NavEntry<NavKey>>,
    ): Scene<NavKey>? {
        if (windowWidth < minWidthDp) return null
        if (destinations.size != entries.size) return null

        return when {
            // Only the list on the back stack.
            destinations.size == 1 && destinations[0] is Destination.UserList -> {
                val listEntry = entries[0]
                ListDetailScene(
                    key = Unit,
                    listEntry = listEntry,
                    detailEntry = null,
                    previousEntries = entries.dropLast(2),
                    emptyDetailContent = emptyDetailContent,
                )
            }

            // List + detail on the back stack.
            destinations.size >= 2 &&
            destinations[destinations.size - 2] is Destination.UserList &&
            destinations.last() is Destination.UserDetail -> {
                val listEntry = entries[entries.size - 2]
                val detailEntry = entries.last()
                ListDetailScene(
                    key = Unit,
                    listEntry = listEntry,
                    detailEntry = detailEntry,
                    previousEntries = entries.dropLast(2),
                    emptyDetailContent = emptyDetailContent,
                )
            }

            else -> null
        }
    }
}

private class ListDetailScene(
    override val key: Any,
    private val listEntry: NavEntry<NavKey>,
    private val detailEntry: NavEntry<NavKey>?,
    override val previousEntries: List<NavEntry<NavKey>>,
    private val emptyDetailContent: @Composable () -> Unit,
) : Scene<NavKey> {
    override val entries: List<NavEntry<NavKey>> = listOfNotNull(listEntry, detailEntry)

    override val content: @Composable () -> Unit = {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                listEntry.Content()
            }
            VerticalDivider()
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                detailEntry?.Content() ?: emptyDetailContent()
            }
        }
    }
}
