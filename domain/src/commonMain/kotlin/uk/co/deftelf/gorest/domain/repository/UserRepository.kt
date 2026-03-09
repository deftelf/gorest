package uk.co.deftelf.gorest.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import uk.co.deftelf.gorest.domain.model.User

interface UserRepository {
    fun observeUsers(): Flow<List<User>>
    suspend fun refreshLastPage(): Result<Unit>
    suspend fun createUser(name: String, email: String, gender: String, birthday: LocalDate): Result<User>
    suspend fun deleteUser(id: Long): Result<Unit>
}
