package uk.co.deftelf.gorest

import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import uk.co.deftelf.gorest.util.toRelativeString
import kotlin.test.Test
import kotlin.test.assertEquals

class RelativeTimestampTest {

    private val now = Instant.parse("2024-06-15T12:00:00Z")

    @Test
    fun under60SecondsIsJustNow() {
        assertEquals("just now", (now - 59.seconds).toRelativeString(now))
    }

    @Test
    fun exactly60SecondsIs1mAgo() {
        assertEquals("1m ago", (now - 60.seconds).toRelativeString(now))
    }

    @Test
    fun minutesBoundary() {
        assertEquals("59m ago", (now - 59.minutes).toRelativeString(now))
    }

    @Test
    fun exactly60MinutesIs1hAgo() {
        assertEquals("1h ago", (now - 60.minutes).toRelativeString(now))
    }

    @Test
    fun hoursBoundary() {
        assertEquals("23h ago", (now - 23.hours).toRelativeString(now))
    }

    @Test
    fun exactly24HoursIs1dAgo() {
        assertEquals("1d ago", (now - 24.hours).toRelativeString(now))
    }

    @Test
    fun daysBoundary() {
        assertEquals("6d ago", (now - 6.days).toRelativeString(now))
    }

    @Test
    fun sevenDaysIsWeeks() {
        assertEquals("1w ago", (now - 7.days).toRelativeString(now))
    }

    @Test
    fun multipleWeeks() {
        assertEquals("2w ago", (now - 14.days).toRelativeString(now))
    }
}
