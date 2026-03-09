package uk.co.deftelf.gorest

import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import uk.co.deftelf.gorest.data.local.CachedUser
import uk.co.deftelf.gorest.data.mapper.UserMapper
import uk.co.deftelf.gorest.data.mapper.UserMapper.toDomain
import uk.co.deftelf.gorest.data.remote.dto.UserDto
import uk.co.deftelf.gorest.domain.model.Gender
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserMapperTest {

    // parseBirthDate

    @Test
    fun parseBirthDateValid() {
        val result = UserMapper.parseBirthDate("2000-5-15")
        assertEquals(Instant.parse("2000-05-15T00:00:00Z"), result)
    }

    @Test
    fun parseBirthDatePadded() {
        val result = UserMapper.parseBirthDate("2000-05-15")
        assertEquals(Instant.parse("2000-05-15T00:00:00Z"), result)
    }

    @Test
    fun parseBirthDateInvalidReturnsEpoch() {
        val result = UserMapper.parseBirthDate("not-a-date")
        assertEquals(Instant.fromEpochMilliseconds(0), result)
    }

    @Test
    fun parseBirthDateEmptyReturnsEpoch() {
        val result = UserMapper.parseBirthDate("")
        assertEquals(Instant.fromEpochMilliseconds(0), result)
    }

    // parseAddUserBirthDate

    @Test
    fun parseAddUserBirthDateValid() {
        val result = UserMapper.parseAddUserBirthDate("2025-MARCH-6")
        assertEquals(Instant.parse("2025-03-06T00:00:00Z"), result)
    }

    @Test
    fun parseAddUserBirthDateEndOfYear() {
        val result = UserMapper.parseAddUserBirthDate("2000-DECEMBER-31")
        assertEquals(Instant.parse("2000-12-31T00:00:00Z"), result)
    }

    @Test
    fun parseAddUserBirthDateInvalidMonthReturnsEpoch() {
        val result = UserMapper.parseAddUserBirthDate("2000-MARCHH-6")
        assertEquals(Instant.fromEpochMilliseconds(0), result)
    }

    // encodeBirthDate

    @Test
    fun encodeBirthDateNonPadded() {
        val result = UserMapper.encodeBirthDate(LocalDate(2000, 5, 7))
        assertEquals("2000-5-7", result)
    }

    // UserDto.toDomain

    @Test
    fun userDtoToDomainConcatenatesName() {
        val dto = UserDto(id = 1, firstName = "John", lastName = "Doe",
            email = "j@test.com", gender = "male", birthDate = "1990-1-1")
        with(UserMapper) {
            val user = dto.toDomain()
            assertEquals("John Doe", user.name)
        }
    }

    @Test
    fun userDtoToDomainInvalidGenderDefaultsMale() {
        val dto = UserDto(id = 1, firstName = "A", lastName = "B",
            email = "a@test.com", gender = "unknown", birthDate = "1990-1-1")
        with(UserMapper) {
            assertEquals(Gender.male, dto.toDomain().gender)
        }
    }

    // CachedUser.toDomain

    @Test
    fun cachedUserToDomainInvalidBirthDateReturnsNull() {
        val cached = CachedUser(id = 1, name = "Test User", email = "t@test.com",
            gender = "male", status = "active", created_at = "", birth_date = "bad")
        with(UserMapper) {
            assertNull(cached.toDomain().birthday)
        }
    }
}
