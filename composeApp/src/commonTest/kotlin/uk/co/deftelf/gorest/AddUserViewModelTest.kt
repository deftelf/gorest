package uk.co.deftelf.gorest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import uk.co.deftelf.gorest.domain.usecase.CreateUserUseCase
import uk.co.deftelf.gorest.presentation.adduser.AddUserIntent
import uk.co.deftelf.gorest.presentation.adduser.AddUserViewModel

class AddUserViewModelTest {

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

    private fun createVm(): Pair<AddUserViewModel, FakeUserRepository> {
        val repo = FakeUserRepository()
        val vm = AddUserViewModel(CreateUserUseCase(repo))
        return vm to repo
    }

    @Test
    fun emptyNameShowsError() = runTest(scheduler) {
        val (vm, _) = createVm()
        vm.processIntent(AddUserIntent.UpdateName(""))
        assertNotNull(vm.state.value.nameError)
    }

    @Test
    fun validNameClearsError() = runTest(scheduler) {
        val (vm, _) = createVm()
        vm.processIntent(AddUserIntent.UpdateName(""))
        vm.processIntent(AddUserIntent.UpdateName("John Doe"))
        assertNull(vm.state.value.nameError)
    }

    @Test
    fun invalidEmailShowsError() = runTest(scheduler) {
        val (vm, _) = createVm()
        vm.processIntent(AddUserIntent.UpdateEmail("not-an-email"))
        assertNotNull(vm.state.value.emailError)
    }

    @Test
    fun validEmailClearsError() = runTest(scheduler) {
        val (vm, _) = createVm()
        vm.processIntent(AddUserIntent.UpdateEmail("not-an-email"))
        vm.processIntent(AddUserIntent.UpdateEmail("valid@example.com"))
        assertNull(vm.state.value.emailError)
    }

    @Test
    fun submitWithEmptyNameShowsError() = runTest(scheduler) {
        val (vm, _) = createVm()
        vm.processIntent(AddUserIntent.UpdateEmail("valid@example.com"))
        vm.processIntent(AddUserIntent.Submit)
        assertNotNull(vm.state.value.nameError)
    }

    @Test
    fun validSubmitSetsSuccess() = runTest(scheduler) {
        val (vm, _) = createVm()
        vm.processIntent(AddUserIntent.UpdateName("John Doe"))
        vm.processIntent(AddUserIntent.UpdateEmail("john@example.com"))
        vm.processIntent(AddUserIntent.Submit)
        advanceUntilIdle()
        assertTrue(vm.state.value.isSuccess)
    }
}
