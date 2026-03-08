package uk.co.deftelf.gorest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import uk.co.deftelf.gorest.domain.model.Gender
import uk.co.deftelf.gorest.domain.model.User
import uk.co.deftelf.gorest.domain.model.UserStatus
import uk.co.deftelf.gorest.domain.usecase.DeleteUserUseCase
import uk.co.deftelf.gorest.domain.usecase.GetUsersUseCase
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedIntent
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class UserFeedViewModelTest {

    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(scheduler)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createUser(id: Long, name: String = "User $id") = User(
        id = id,
        name = name,
        email = "user$id@test.com",
        gender = Gender.male,
        status = UserStatus.active,
        createdAt = Instant.parse("2024-01-01T00:00:00Z"),
    )

    @Test
    fun initialLoadTriggersRefresh() = runTest(scheduler) {
        val repo = FakeUserRepository()
        val vm = UserFeedViewModel(GetUsersUseCase(repo), DeleteUserUseCase(repo))
        advanceUntilIdle()
        assertEquals(1, repo.refreshCount)
    }

    @Test
    fun confirmDeleteRemovesUserOptimistically() = runTest(scheduler) {
        val repo = FakeUserRepository()
        repo.setUsers(listOf(createUser(1), createUser(2), createUser(3)))
        val vm = UserFeedViewModel(GetUsersUseCase(repo), DeleteUserUseCase(repo))
        advanceUntilIdle()

        vm.processIntent(UserFeedIntent.ConfirmDelete(2L))
        advanceUntilIdle()

        assertTrue(vm.state.value.users.none { it.id == 2L })
        assertEquals(2, vm.state.value.users.size)
    }

    @Test
    fun undoDeleteRestoresUser() = runTest(scheduler) {
        val repo = FakeUserRepository()
        repo.setUsers(listOf(createUser(1), createUser(2), createUser(3)))
        val vm = UserFeedViewModel(GetUsersUseCase(repo), DeleteUserUseCase(repo))
        advanceUntilIdle()

        vm.processIntent(UserFeedIntent.ConfirmDelete(2L))
        runCurrent()
        assertTrue(vm.state.value.users.none { it.id == 2L })

        vm.processIntent(UserFeedIntent.UndoDelete(2L))
        runCurrent()
        assertTrue(vm.state.value.users.any { it.id == 2L })
        assertEquals(3, vm.state.value.users.size)
    }

    @Test
    fun commitDeleteAfter5SecondsCallsRepo() = runTest(scheduler) {
        val repo = FakeUserRepository()
        repo.setUsers(listOf(createUser(1), createUser(2)))
        val vm = UserFeedViewModel(GetUsersUseCase(repo), DeleteUserUseCase(repo))
        advanceUntilIdle()

        vm.processIntent(UserFeedIntent.ConfirmDelete(1L))
        runCurrent()
        assertTrue(repo.deletedIds.isEmpty())

        advanceTimeBy(5_001)
        advanceUntilIdle()
        assertEquals(listOf(1L), repo.deletedIds)
    }

    @Test
    fun networkFailureOnLoadSetsError() = runTest(scheduler) {
        val repo = FakeUserRepository().apply { shouldFailRefresh = true }
        val vm = UserFeedViewModel(GetUsersUseCase(repo), DeleteUserUseCase(repo))
        advanceUntilIdle()
        assertTrue(vm.state.value.error != null)
    }
}
