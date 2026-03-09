package uk.co.deftelf.gorest.domain.usecase

import kotlinx.datetime.LocalDate
import uk.co.deftelf.gorest.domain.model.User
import uk.co.deftelf.gorest.domain.repository.UserRepository

class CreateUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(name: String, email: String, gender: String, birthday: LocalDate): Result<User> {
        if (name.isBlank()) return Result.failure(IllegalArgumentException("Name cannot be empty"))
        if (name.trim().split("\\s+".toRegex()).size != 2) return Result.failure(IllegalArgumentException("Name must be a first and last name"))
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (!emailRegex.matches(email)) return Result.failure(IllegalArgumentException("Invalid email address"))
        return repository.createUser(name, email, gender, birthday)
    }
}
