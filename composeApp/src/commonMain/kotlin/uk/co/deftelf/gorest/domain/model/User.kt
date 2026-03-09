package uk.co.deftelf.gorest.domain.model

import kotlin.time.Instant

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val gender: Gender,
    val birthday: Instant,
)

enum class Gender { male, female }
