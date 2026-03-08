package uk.co.deftelf.gorest.util

import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.Duration

fun Instant.toRelativeString(now: Instant = Clock.System.now()): String {
    val diff: Duration = now - this
    return when {
        diff.inWholeSeconds < 60  -> "just now"
        diff.inWholeMinutes < 60  -> "${diff.inWholeMinutes}m ago"
        diff.inWholeHours < 24    -> "${diff.inWholeHours}h ago"
        diff.inWholeDays < 7      -> "${diff.inWholeDays}d ago"
        else                      -> "${diff.inWholeDays / 7}w ago"
    }
}
