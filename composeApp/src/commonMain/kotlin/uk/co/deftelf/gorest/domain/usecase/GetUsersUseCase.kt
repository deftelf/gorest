package uk.co.deftelf.gorest.domain.usecase

import kotlinx.coroutines.flow.Flow
import uk.co.deftelf.gorest.domain.model.User
import uk.co.deftelf.gorest.domain.repository.UserRepository

class GetUsersUseCase(private val repository: UserRepository) {
    operator fun invoke(): Flow<List<User>> = repository.observeUsers()
    suspend fun refresh(): Result<Unit> = repository.refreshLastPage()
}
