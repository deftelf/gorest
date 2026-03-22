package uk.co.deftelf.gorest

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import uk.co.deftelf.gorest.domain.model.Gender
import uk.co.deftelf.gorest.domain.model.User
import uk.co.deftelf.gorest.domain.repository.UserRepository

class FakeUserRepository : UserRepository {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val deletedIds = mutableListOf<Long>()
    var refreshCount = 0
    var createdCount = 0
    var shouldFailRefresh = false
    var shouldFailCreate = false
    var shouldFailDelete = false

    fun setUsers(users: List<User>) {
        _users.value = users
    }

    override fun observeUsers(): Flow<List<User>> = _users.asStateFlow()

    override suspend fun refreshLastPage(): Result<Unit> {
        refreshCount++
        return if (shouldFailRefresh) Result.failure(Exception("Network error"))
        else Result.success(Unit)
    }

    override suspend fun createUser(
        name: String,
        email: String,
        gender: String,
        birthday: LocalDate,
    ): Result<User> {
        if (shouldFailCreate) return Result.failure(Exception("Create failed"))
        createdCount++
        val user = User(
            id = System.currentTimeMillis(),
            name = name,
            email = email,
            gender = Gender.valueOf(gender),
            birthday = Instant.fromEpochMilliseconds(0),
        )
        _users.value = listOf(user) + _users.value
        return Result.success(user)
    }

    override suspend fun deleteUser(id: Long): Result<Unit> {
        if (shouldFailDelete) return Result.failure(Exception("Delete failed"))
        deletedIds.add(id)
        _users.value = _users.value.filter { it.id != id }
        return Result.success(Unit)
    }
}
