package uk.co.deftelf.gorest.data.mapper

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.number
import kotlin.time.Instant
import uk.co.deftelf.gorest.data.local.CachedUser
import uk.co.deftelf.gorest.data.remote.dto.UserDto
import uk.co.deftelf.gorest.domain.model.Gender
import uk.co.deftelf.gorest.domain.model.User

object UserMapper {

    fun UserDto.toDomain(): User = User(
        id = id,
        name = "$firstName $lastName".trim(),
        email = email,
        gender = runCatching { Gender.valueOf(gender) }.getOrElse { Gender.male },
        birthday = parseBirthDate(birthDate),
    )

    fun CachedUser.toDomain(): User = User(
        id = id,
        name = name,
        email = email,
        gender = runCatching { Gender.valueOf(gender) }.getOrElse { Gender.male },
        birthday = runCatching { Instant.parse(birth_date) }.getOrNull(),
    )

    /** Parses DummyJSON's non-padded "yyyy-M-d" date string to an Instant. */
    fun parseBirthDate(dateStr: String): Instant = runCatching {
        val parts = dateStr.split("-")
        LocalDate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            .atStartOfDayIn(TimeZone.UTC)
    }.getOrElse { Instant.fromEpochMilliseconds(0) }

    /** Parses the "yyyy-MONTHNAME-d" format returned by DummyJSON's add-user endpoint. */
    fun parseAddUserBirthDate(dateStr: String): Instant = runCatching {
        val parts = dateStr.split("-")
        LocalDate(parts[0].toInt(), Month.valueOf(parts[1]), parts[2].toInt())
            .atStartOfDayIn(TimeZone.UTC)
    }.getOrElse { Instant.fromEpochMilliseconds(0) }

    /** Encodes DummyJSON's non-padded "yyyy-M-d" date string. */
    fun encodeBirthDate(date: LocalDate): String = runCatching {
        "${date.year}-${date.month.number}-${date.day}"
    }.getOrElse {""}
}
