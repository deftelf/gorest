package uk.co.deftelf.gorest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import uk.co.deftelf.gorest.domain.usecase.CreateUserUseCase
import uk.co.deftelf.gorest.presentation.adduser.AddUserEffect
import uk.co.deftelf.gorest.presentation.adduser.AddUserUiEvent
import uk.co.deftelf.gorest.presentation.adduser.AddUserViewModel

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
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
        vm.processIntent(AddUserUiEvent.UpdateName(""))
        runCurrent()
        assertNotNull(vm.state.value.nameError)
    }

    @Test
    fun validNameClearsError() = runTest(scheduler) {
        val (vm, _) = createVm()
        vm.processIntent(AddUserUiEvent.UpdateName(""))
        vm.processIntent(AddUserUiEvent.UpdateName("John Doe"))
        assertNull(vm.state.value.nameError)
    }

    @Test
    fun invalidEmailShowsError() = runTest(scheduler) {
        val (vm, _) = createVm()
        vm.processIntent(AddUserUiEvent.UpdateEmail("not-an-email"))
        runCurrent()
        assertNotNull(vm.state.value.emailError)
    }

    @Test
    fun validEmailClearsError() = runTest(scheduler) {
        val (vm, _) = createVm()
        vm.processIntent(AddUserUiEvent.UpdateEmail("not-an-email"))
        vm.processIntent(AddUserUiEvent.UpdateEmail("valid@example.com"))
        assertNull(vm.state.value.emailError)
    }

    @Test
    fun submitWithEmptyNameShowsError() = runTest(scheduler) {
        val (vm, _) = createVm()
        vm.processIntent(AddUserUiEvent.UpdateEmail("valid@example.com"))
        vm.processIntent(AddUserUiEvent.Submit)
        runCurrent()
        assertNotNull(vm.state.value.nameError)
    }

    @Test
    fun validSubmitSetsSuccess() = runTest(scheduler) {
        val (vm, repo) = createVm()
        vm.processIntent(AddUserUiEvent.UpdateName("John Doe"))
        vm.processIntent(AddUserUiEvent.UpdateEmail("john@example.com"))
        vm.processIntent(AddUserUiEvent.UpdateBirthday(LocalDate(1990, 1, 1)))
        vm.processIntent(AddUserUiEvent.Submit)
        advanceUntilIdle()
        assertEquals(1, repo.createdCount)
    }

    @Test
    fun submitWithoutBirthdayShowsBirthdayError() = runTest(scheduler) {
        val (vm, _) = createVm()
        vm.processIntent(AddUserUiEvent.UpdateName("John Doe"))
        vm.processIntent(AddUserUiEvent.UpdateEmail("john@example.com"))
        vm.processIntent(AddUserUiEvent.Submit)
        runCurrent()
        assertNotNull(vm.state.value.birthdayError)
    }

    @Test
    fun submitWithRepositoryFailureShowsGeneralError() = runTest(scheduler) {
        val repo = FakeUserRepository().apply { shouldFailCreate = true }
        val vm = AddUserViewModel(CreateUserUseCase(repo))
        vm.processIntent(AddUserUiEvent.UpdateName("John Doe"))
        vm.processIntent(AddUserUiEvent.UpdateEmail("john@example.com"))
        vm.processIntent(AddUserUiEvent.UpdateBirthday(LocalDate(1990, 1, 1)))
        vm.processIntent(AddUserUiEvent.Submit)
        advanceUntilIdle()
        assertNotNull(vm.state.value.generalError)
    }

    @Test
    fun showDatePickerUpdatesState() = runTest(scheduler) {
        val (vm, _) = createVm()
        vm.processIntent(AddUserUiEvent.ShowDatePicker)
        runCurrent()
        assertTrue(vm.state.value.showDatePicker)
        vm.processIntent(AddUserUiEvent.HideDatePicker)
        runCurrent()
        assertTrue(!vm.state.value.showDatePicker)
    }

    @Test
    fun validSubmitSendsNavigateBackEffect() = runTest(scheduler) {
        val (vm, _) = createVm()
        val effects = mutableListOf<AddUserEffect>()
        val job = launch { vm.effects.collect { effects.add(it) } }
        vm.processIntent(AddUserUiEvent.UpdateName("John Doe"))
        vm.processIntent(AddUserUiEvent.UpdateEmail("john@example.com"))
        vm.processIntent(AddUserUiEvent.UpdateBirthday(LocalDate(1990, 1, 1)))
        vm.processIntent(AddUserUiEvent.Submit)
        advanceUntilIdle()
        assertTrue(effects.any { it is AddUserEffect.NavigateBack })
        job.cancel()
    }
}
