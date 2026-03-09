package uk.co.deftelf.gorest

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import uk.co.deftelf.gorest.domain.usecase.CreateUserUseCase
import kotlin.test.Test
import kotlin.test.assertTrue

class CreateUserUseCaseTest {

    private val repo = FakeUserRepository()
    private val useCase = CreateUserUseCase(repo)

    @Test
    fun emptyNameReturnsFailure() = runTest {
        val result = useCase("", "test@example.com", "male", LocalDate(1990, 1, 1))
        assertTrue(result.isFailure)
    }

    @Test
    fun blankNameReturnsFailure() = runTest {
        val result = useCase("   ", "test@example.com", "male", LocalDate(1990, 1, 1))
        assertTrue(result.isFailure)
    }

    @Test
    fun malformedEmailReturnsFailure() = runTest {
        val result = useCase("John Doe", "not-an-email", "male", LocalDate(1990, 1, 1))
        assertTrue(result.isFailure)
    }

    @Test
    fun emailWithoutDomainReturnsFailure() = runTest {
        val result = useCase("John Doe", "john@", "male", LocalDate(1990, 1, 1))
        assertTrue(result.isFailure)
    }

    @Test
    fun emailWithoutTldReturnsFailure() = runTest {
        val result = useCase("John Doe", "john@example", "male", LocalDate(1990, 1, 1))
        assertTrue(result.isFailure)
    }

    @Test
    fun validInputReturnsSuccess() = runTest {
        val result = useCase("John Doe", "john@example.com", "male", LocalDate(1990, 1, 1))
        assertTrue(result.isSuccess)
    }

    @Test
    fun longNameReturnsSuccess() = runTest {
        val longName = "A".repeat(100)
        val result = useCase(longName, "john@example.com", "male", LocalDate(1990, 1, 1))
        assertTrue(result.isSuccess)
    }
}
