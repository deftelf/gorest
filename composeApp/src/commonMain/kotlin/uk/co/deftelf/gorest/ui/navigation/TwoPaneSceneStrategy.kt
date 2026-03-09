package uk.co.deftelf.gorest.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

/**
 * A [SceneStrategy] that displays the bottom two back-stack entries side by side when the
 * available width is at least [minWidthDp] (list on the left, detail on the right).
 * On narrower displays it falls back to showing only the top entry full-screen.
 * Returns null for a single back-stack entry so the strategy chain falls through to
 * [androidx.navigation3.scene.SinglePaneSceneStrategy].
 */
class TwoPaneSceneStrategy<T : Any>(private val minWidthDp: Dp = 600.dp) : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (entries.size < 2) return null
        val listEntry = entries[entries.size - 2]
        val detailEntry = entries.last()
        return TwoPaneScene(
            key = listEntry to detailEntry,
            listEntry = listEntry,
            detailEntry = detailEntry,
            previousEntries = entries.dropLast(2),
            minWidthDp = minWidthDp,
        )
    }
}

private class TwoPaneScene<T : Any>(
    override val key: Any,
    private val listEntry: NavEntry<T>,
    private val detailEntry: NavEntry<T>,
    override val previousEntries: List<NavEntry<T>>,
    private val minWidthDp: Dp,
) : Scene<T> {

    override val entries: List<NavEntry<T>> = listOf(listEntry, detailEntry)

    override val content: @Composable () -> Unit = {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            if (maxWidth >= minWidthDp) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        listEntry.Content()
                    }
                    VerticalDivider()
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        detailEntry.Content()
                    }
                }
            } else {
                detailEntry.Content()
            }
        }
    }
}
