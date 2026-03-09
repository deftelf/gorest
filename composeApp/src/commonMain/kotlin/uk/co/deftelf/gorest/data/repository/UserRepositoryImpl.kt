package uk.co.deftelf.gorest.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uk.co.deftelf.gorest.data.local.GoRestDatabase
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import uk.co.deftelf.gorest.data.mapper.UserMapper
import uk.co.deftelf.gorest.data.mapper.UserMapper.parseBirthDate
import uk.co.deftelf.gorest.data.mapper.UserMapper.toDomain
import uk.co.deftelf.gorest.data.remote.DummyJsonApiService
import uk.co.deftelf.gorest.domain.model.User
import uk.co.deftelf.gorest.domain.repository.UserRepository

class UserRepositoryImpl(
    private val api: DummyJsonApiService,
    private val db: GoRestDatabase,
) : UserRepository {

    override fun observeUsers(): Flow<List<User>> =
        db.goRestDatabaseQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun refreshLastPage(): Result<Unit> = runCatching {
        val users = api.fetchUsers()
        db.goRestDatabaseQueries.transaction {
            db.goRestDatabaseQueries.deleteAll()
            users.forEach { dto ->
                db.goRestDatabaseQueries.insertOrReplace(
                    id = dto.id,
                    name = "${dto.firstName} ${dto.lastName}".trim(),
                    email = dto.email,
                    gender = dto.gender,
                    status = "",
                    created_at = "",
                    birth_date = parseBirthDate(dto.birthDate).toString(),
                )
            }
        }
    }

    override suspend fun createUser(
        name: String,
        email: String,
        gender: String,
        birthday: LocalDate,
    ): Result<User> = runCatching {
        val parts = name.trim().split(" ", limit = 2)
        val firstName = parts[0]
        val lastName = parts.getOrElse(1) { "" }
        val encodedBirthday = UserMapper.encodeBirthDate(birthday)
        val created = api.createUser(firstName, lastName, email, gender, encodedBirthday)
        val createdBirthday = parseBirthDate(created.birthDate)
        db.goRestDatabaseQueries.insertOrReplace(
            id = created.id,
            name = "${created.firstName} ${created.lastName}".trim(),
            email = created.email,
            gender = created.gender,
            status = "",
            created_at = "",
            birth_date = createdBirthday.toString(),
        )
        created.toDomain()
    }

    override suspend fun deleteUser(id: Long): Result<Unit> = runCatching {
        api.deleteUser(id)
        db.goRestDatabaseQueries.deleteById(id)
    }
}
