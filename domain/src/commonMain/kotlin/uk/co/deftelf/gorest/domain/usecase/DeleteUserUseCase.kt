package uk.co.deftelf.gorest.domain.usecase

import uk.co.deftelf.gorest.domain.repository.UserRepository

class DeleteUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> = repository.deleteUser(id)
}
