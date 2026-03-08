package uk.co.deftelf.gorest

import kotlinx.coroutines.test.runTest
import uk.co.deftelf.gorest.domain.usecase.CreateUserUseCase
import kotlin.test.Test
import kotlin.test.assertTrue

class CreateUserUseCaseTest {

    private val repo = FakeUserRepository()
    private val useCase = CreateUserUseCase(repo)

    @Test
    fun emptyNameReturnsFailure() = runTest {
        val result = useCase("", "test@example.com", "male", "active")
        assertTrue(result.isFailure)
    }

    @Test
    fun blankNameReturnsFailure() = runTest {
        val result = useCase("   ", "test@example.com", "male", "active")
        assertTrue(result.isFailure)
    }

    @Test
    fun malformedEmailReturnsFailure() = runTest {
        val result = useCase("John Doe", "not-an-email", "male", "active")
        assertTrue(result.isFailure)
    }

    @Test
    fun emailWithoutDomainReturnsFailure() = runTest {
        val result = useCase("John Doe", "john@", "male", "active")
        assertTrue(result.isFailure)
    }

    @Test
    fun emailWithoutTldReturnsFailure() = runTest {
        val result = useCase("John Doe", "john@example", "male", "active")
        assertTrue(result.isFailure)
    }

    @Test
    fun validInputReturnsSuccess() = runTest {
        val result = useCase("John Doe", "john@example.com", "male", "active")
        assertTrue(result.isSuccess)
    }

    @Test
    fun longNameReturnsSuccess() = runTest {
        val longName = "A".repeat(100)
        val result = useCase(longName, "john@example.com", "male", "active")
        assertTrue(result.isSuccess)
    }
}
