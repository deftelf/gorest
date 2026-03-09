package uk.co.deftelf.gorest

import kotlinx.coroutines.test.runTest
import uk.co.deftelf.gorest.domain.usecase.DeleteUserUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteUserUseCaseTest {

    @Test
    fun successDelegatesToRepository() = runTest {
        val repo = FakeUserRepository()
        val useCase = DeleteUserUseCase(repo)
        val result = useCase(42L)
        assertTrue(result.isSuccess)
        assertEquals(listOf(42L), repo.deletedIds)
    }

    @Test
    fun failurePropagates() = runTest {
        val repo = FakeUserRepository().apply { shouldFailDelete = true }
        val useCase = DeleteUserUseCase(repo)
        val result = useCase(1L)
        assertTrue(result.isFailure)
    }
}
