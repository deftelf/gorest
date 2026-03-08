package uk.co.deftelf.gorest.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uk.co.deftelf.gorest.data.local.GoRestDatabase
import uk.co.deftelf.gorest.data.mapper.UserMapper.toDomain
import uk.co.deftelf.gorest.data.remote.GoRestApiService
import uk.co.deftelf.gorest.data.remote.dto.UserDto
import uk.co.deftelf.gorest.domain.model.User
import uk.co.deftelf.gorest.domain.repository.UserRepository
import uk.co.deftelf.gorest.util.AuthTokenProvider
import kotlin.time.Clock

class UserRepositoryImpl(
    private val api: GoRestApiService,
    private val db: GoRestDatabase,
    private val tokenProvider: AuthTokenProvider,
) : UserRepository {

    override fun observeUsers(): Flow<List<User>> =
        db.goRestDatabaseQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun refreshLastPage(): Result<Unit> = runCatching {
        val users = api.fetchLastPageUsers()
        db.goRestDatabaseQueries.transaction {
            db.goRestDatabaseQueries.deleteAll()
            users.forEach { dto ->
                db.goRestDatabaseQueries.insertOrReplace(
                    id = dto.id,
                    name = dto.name,
                    email = dto.email,
                    gender = dto.gender,
                    status = dto.status,
                    created_at = dto.createdAt,
                )
            }
        }
    }

    override suspend fun createUser(
        name: String,
        email: String,
        gender: String,
        status: String,
    ): Result<User> = runCatching {
        val dto = UserDto(
            id = 0L,
            name = name,
            email = email,
            gender = gender,
            status = status,
            createdAt = Clock.System.now().toString(),
        )
        val created = api.createUser(dto, tokenProvider.getToken())
        db.goRestDatabaseQueries.insertOrReplace(
            id = created.id,
            name = created.name,
            email = created.email,
            gender = created.gender,
            status = created.status,
            created_at = created.createdAt,
        )
        created.toDomain()
    }

    override suspend fun deleteUser(id: Long): Result<Unit> = runCatching {
        api.deleteUser(id, tokenProvider.getToken())
        db.goRestDatabaseQueries.deleteById(id)
    }
}
