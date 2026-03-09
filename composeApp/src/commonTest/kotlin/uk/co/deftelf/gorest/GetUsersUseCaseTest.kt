package uk.co.deftelf.gorest

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import uk.co.deftelf.gorest.domain.model.Gender
import uk.co.deftelf.gorest.domain.model.User
import uk.co.deftelf.gorest.domain.usecase.GetUsersUseCase
import kotlin.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetUsersUseCaseTest {

    private fun makeUser(id: Long) = User(
        id = id, name = "User $id", email = "user$id@test.com",
        gender = Gender.male, birthday = Instant.parse("1990-01-01T00:00:00Z"),
    )

    @Test
    fun invokeEmitsRepositoryFlow() = runTest {
        val repo = FakeUserRepository()
        val users = listOf(makeUser(1), makeUser(2))
        repo.setUsers(users)
        val useCase = GetUsersUseCase(repo)
        val emitted = mutableListOf<List<User>>()
        val job = launch { useCase().collect { emitted.add(it) } }
        advanceUntilIdle()
        job.cancel()
        assertEquals(users, emitted.first())
    }

    @Test
    fun refreshSuccessReturnsSuccess() = runTest {
        val useCase = GetUsersUseCase(FakeUserRepository())
        assertTrue(useCase.refresh().isSuccess)
    }

    @Test
    fun refreshFailurePropagates() = runTest {
        val useCase = GetUsersUseCase(FakeUserRepository().apply { shouldFailRefresh = true })
        assertTrue(useCase.refresh().isFailure)
    }
}
