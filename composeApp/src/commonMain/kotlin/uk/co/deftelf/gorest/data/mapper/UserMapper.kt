package uk.co.deftelf.gorest.data.mapper

import kotlin.time.Instant
import uk.co.deftelf.gorest.data.local.CachedUser
import uk.co.deftelf.gorest.data.remote.dto.UserDto
import uk.co.deftelf.gorest.domain.model.Gender
import uk.co.deftelf.gorest.domain.model.User
import uk.co.deftelf.gorest.domain.model.UserStatus

object UserMapper {

    fun UserDto.toDomain(): User = User(
        id = id,
        name = name,
        email = email,
        gender = Gender.valueOf(gender),
        status = UserStatus.valueOf(status),
        createdAt = runCatching { Instant.parse(createdAt) }.getOrElse { Instant.fromEpochMilliseconds(0) },
    )

    fun CachedUser.toDomain(): User = User(
        id = id,
        name = name,
        email = email,
        gender = Gender.valueOf(gender),
        status = UserStatus.valueOf(status),
        createdAt = runCatching { Instant.parse(created_at) }.getOrElse { Instant.fromEpochMilliseconds(0) },
    )

    fun User.toDto(): UserDto = UserDto(
        id = id,
        name = name,
        email = email,
        gender = gender.name,
        status = status.name,
        createdAt = createdAt.toString(),
    )
}
