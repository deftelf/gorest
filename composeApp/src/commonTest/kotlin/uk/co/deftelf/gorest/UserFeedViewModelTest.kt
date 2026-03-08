package uk.co.deftelf.gorest

import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceTimeBy
import kotlin.time.Instant
import uk.co.deftelf.gorest.domain.model.Gender
import uk.co.deftelf.gorest.domain.model.User
import uk.co.deftelf.gorest.domain.model.UserStatus
import uk.co.deftelf.gorest.domain.usecase.DeleteUserUseCase
import uk.co.deftelf.gorest.domain.usecase.GetUsersUseCase
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedIntent
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserFeedViewModelTest {

    private fun createUser(id: Long, name: String = "User $id") = User(
        id = id,
        name = name,
        email = "user$id@test.com",
        gender = Gender.male,
        status = UserStatus.active,
        createdAt = Instant.parse("2024-01-01T00:00:00Z"),
    )

    @Test
    fun initialLoadTriggersRefresh() = runTest {
        val repo = FakeUserRepository()
        val vm = UserFeedViewModel(GetUsersUseCase(repo), DeleteUserUseCase(repo))
        assertEquals(1, repo.refreshCount)
    }

    @Test
    fun confirmDeleteRemovesUserOptimistically() = runTest {
        val repo = FakeUserRepository()
        val users = listOf(createUser(1), createUser(2), createUser(3))
        repo.setUsers(users)
        val vm = UserFeedViewModel(GetUsersUseCase(repo), DeleteUserUseCase(repo))

        vm.processIntent(UserFeedIntent.ConfirmDelete(2L))

        // After optimistic removal, user 2 should not be in state
        assertTrue(vm.state.value.users.none { it.id == 2L })
        assertEquals(2, vm.state.value.users.size)
    }

    @Test
    fun undoDeleteRestoresUser() = runTest {
        val repo = FakeUserRepository()
        val users = listOf(createUser(1), createUser(2), createUser(3))
        repo.setUsers(users)
        val vm = UserFeedViewModel(GetUsersUseCase(repo), DeleteUserUseCase(repo))

        vm.processIntent(UserFeedIntent.ConfirmDelete(2L))
        assertTrue(vm.state.value.users.none { it.id == 2L })

        vm.processIntent(UserFeedIntent.UndoDelete(2L))
        assertTrue(vm.state.value.users.any { it.id == 2L })
        assertEquals(3, vm.state.value.users.size)
    }

    @Test
    fun commitDeleteAfter5SecondsCallsRepo() = runTest {
        val repo = FakeUserRepository()
        val users = listOf(createUser(1), createUser(2))
        repo.setUsers(users)
        val vm = UserFeedViewModel(GetUsersUseCase(repo), DeleteUserUseCase(repo))

        vm.processIntent(UserFeedIntent.ConfirmDelete(1L))
        assertTrue(repo.deletedIds.isEmpty())

        advanceTimeBy(5_001)
        assertEquals(listOf(1L), repo.deletedIds)
    }

    @Test
    fun networkFailureOnLoadSetsError() = runTest {
        val repo = FakeUserRepository().apply { shouldFailRefresh = true }
        val vm = UserFeedViewModel(GetUsersUseCase(repo), DeleteUserUseCase(repo))
        assertTrue(vm.state.value.error != null)
    }
}
