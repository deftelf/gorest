package uk.co.deftelf.gorest.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import uk.co.deftelf.gorest.data.remote.dto.CreateUserDto
import uk.co.deftelf.gorest.data.remote.dto.UserDto
import uk.co.deftelf.gorest.data.remote.dto.UsersResponse

class DummyJsonApiService(private val client: HttpClient) {

    suspend fun fetchUsers(): List<UserDto> =
        client.get("users").body<UsersResponse>().users

    suspend fun createUser(firstName: String, lastName: String, email: String, gender: String): UserDto =
        client.post("users/add") {
            contentType(ContentType.Application.Json)
            setBody(CreateUserDto(firstName, lastName, email, gender))
        }.body()

    suspend fun deleteUser(id: Long) {
        client.delete("users/$id")
    }
}
