package uk.co.deftelf.gorest.domain.model

import kotlin.time.Instant

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val gender: Gender,
    val status: UserStatus,
    val createdAt: Instant,
)

enum class Gender { male, female }

enum class UserStatus { active, inactive }
