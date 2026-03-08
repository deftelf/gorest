package uk.co.deftelf.gorest.presentation.adduser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.co.deftelf.gorest.domain.model.Gender
import uk.co.deftelf.gorest.domain.model.UserStatus
import uk.co.deftelf.gorest.domain.usecase.CreateUserUseCase

class AddUserViewModel(
    private val createUserUseCase: CreateUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AddUserState())
    val state: StateFlow<AddUserState> = _state.asStateFlow()

    private val _effects = Channel<AddUserEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun processIntent(intent: AddUserIntent) {
        when (intent) {
            is AddUserIntent.UpdateName -> {
                val error = if (intent.value.isBlank()) "Name cannot be empty" else null
                _state.update { it.copy(name = intent.value, nameError = error) }
            }
            is AddUserIntent.UpdateEmail -> {
                val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                val error = if (!emailRegex.matches(intent.value)) "Invalid email address" else null
                _state.update { it.copy(email = intent.value, emailError = error) }
            }
            is AddUserIntent.UpdateGender -> {
                _state.update { it.copy(gender = intent.gender) }
            }
            is AddUserIntent.UpdateStatus -> {
                _state.update { it.copy(status = intent.status) }
            }
            is AddUserIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val current = _state.value
        val nameError = if (current.name.isBlank()) "Name cannot be empty" else null
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        val emailError = if (!emailRegex.matches(current.email)) "Invalid email address" else null
        if (nameError != null || emailError != null) {
            _state.update { it.copy(nameError = nameError, emailError = emailError) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            createUserUseCase(current.name, current.email, current.gender.name, current.status.name)
                .onSuccess {
                    _state.update { it.copy(isSubmitting = false, isSuccess = true) }
                    _effects.send(AddUserEffect.NavigateBack)
                }
                .onFailure { e ->
                    _state.update { it.copy(isSubmitting = false, emailError = e.message) }
                }
        }
    }
}
