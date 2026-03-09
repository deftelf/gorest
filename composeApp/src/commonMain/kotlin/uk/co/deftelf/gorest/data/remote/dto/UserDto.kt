package uk.co.deftelf.gorest.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    val email: String,
    val gender: String,
    @SerialName("birthDate") val birthDate: String,
)

@Serializable
data class UsersResponse(
    val users: List<UserDto>,
)

@Serializable
data class CreateUserDto(
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    val email: String,
    val gender: String,
    @SerialName("birthDate") val birthDate: String,
)
