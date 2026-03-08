package uk.co.deftelf.gorest.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import uk.co.deftelf.gorest.data.remote.dto.UserDto

class GoRestApiService(private val client: HttpClient) {

    suspend fun fetchLastPageUsers(): List<UserDto> {
        val firstPage: HttpResponse = client.get("users") {
            url { parameters.append("page", "1") }
        }
        val totalPages = firstPage.headers["X-Pagination-Pages"]?.toIntOrNull() ?: 1
        if (totalPages <= 1) return firstPage.body()
        val lastPage: HttpResponse = client.get("users") {
            url { parameters.append("page", totalPages.toString()) }
        }
        return lastPage.body()
    }

    suspend fun createUser(dto: UserDto, token: String): UserDto {
        return client.post("users") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()
    }

    suspend fun deleteUser(id: Long, token: String) {
        client.delete("users/$id") {
            bearerAuth(token)
        }
    }
}
