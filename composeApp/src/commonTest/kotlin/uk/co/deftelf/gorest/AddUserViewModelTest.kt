package uk.co.deftelf.gorest

import kotlinx.coroutines.test.runTest
import uk.co.deftelf.gorest.domain.usecase.CreateUserUseCase
import uk.co.deftelf.gorest.presentation.adduser.AddUserIntent
import uk.co.deftelf.gorest.presentation.adduser.AddUserViewModel
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AddUserViewModelTest {

    private fun createVm(): Pair<AddUserViewModel, FakeUserRepository> {
        val repo = FakeUserRepository()
        val vm = AddUserViewModel(CreateUserUseCase(repo))
        return vm to repo
    }

    @Test
    fun emptyNameShowsError() = runTest {
        val (vm, _) = createVm()
        vm.processIntent(AddUserIntent.UpdateName(""))
        assertNotNull(vm.state.value.nameError)
    }

    @Test
    fun validNameClearsError() = runTest {
        val (vm, _) = createVm()
        vm.processIntent(AddUserIntent.UpdateName(""))
        vm.processIntent(AddUserIntent.UpdateName("John Doe"))
        assertNull(vm.state.value.nameError)
    }

    @Test
    fun invalidEmailShowsError() = runTest {
        val (vm, _) = createVm()
        vm.processIntent(AddUserIntent.UpdateEmail("not-an-email"))
        assertNotNull(vm.state.value.emailError)
    }

    @Test
    fun validEmailClearsError() = runTest {
        val (vm, _) = createVm()
        vm.processIntent(AddUserIntent.UpdateEmail("not-an-email"))
        vm.processIntent(AddUserIntent.UpdateEmail("valid@example.com"))
        assertNull(vm.state.value.emailError)
    }

    @Test
    fun submitWithEmptyNameShowsError() = runTest {
        val (vm, _) = createVm()
        vm.processIntent(AddUserIntent.UpdateEmail("valid@example.com"))
        vm.processIntent(AddUserIntent.Submit)
        assertNotNull(vm.state.value.nameError)
    }

    @Test
    fun validSubmitSetsSuccess() = runTest {
        val (vm, _) = createVm()
        vm.processIntent(AddUserIntent.UpdateName("John Doe"))
        vm.processIntent(AddUserIntent.UpdateEmail("john@example.com"))
        vm.processIntent(AddUserIntent.Submit)
        assertTrue(vm.state.value.isSuccess)
    }
}
