package uk.co.deftelf.gorest.ui.util

import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.ageInYears(): Int {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val birth = toLocalDateTime(TimeZone.currentSystemDefault())
    var age = today.year - birth.year
    if (today.month < birth.month ||
        (today.month == birth.month && today.day < birth.day)
    ) age--
    return age
}
