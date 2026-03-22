package uk.co.deftelf.gorest.presentation.adduser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import uk.co.deftelf.gorest.domain.model.Gender
import uk.co.deftelf.gorest.domain.usecase.CreateUserUseCase

class AddUserViewModel(
    private val createUserUseCase: CreateUserUseCase,
) : ViewModel() {

    private val name = MutableStateFlow("")
    private val email = MutableStateFlow("")
    private val gender = MutableStateFlow(Gender.male)
    private val birthday = MutableStateFlow<LocalDate?>(null)
    private val showDatePicker = MutableStateFlow(false)
    private val nameError = MutableStateFlow<String?>(null)
    private val emailError = MutableStateFlow<String?>(null)
    private val birthdayError = MutableStateFlow<String?>(null)
    private val isSubmitting = MutableStateFlow(false)
    private val isSuccess = MutableStateFlow(false)
    private val generalError = MutableStateFlow<String?>(null)

    val state: StateFlow<AddUserUiState> = combine(
        name as Flow<Any?>, nameError, email, emailError, gender,
        birthday, birthdayError, showDatePicker, isSubmitting, isSuccess, generalError,
    ) { v ->
        AddUserUiState(
            name = v[0] as String,
            nameError = v[1] as String?,
            email = v[2] as String,
            emailError = v[3] as String?,
            gender = v[4] as Gender,
            birthday = v[5] as LocalDate?,
            birthdayError = v[6] as String?,
            showDatePicker = v[7] as Boolean,
            isSubmitting = v[8] as Boolean,
            isSuccess = v[9] as Boolean,
            generalError = v[10] as String?,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AddUserUiState())

    private val _effects = Channel<AddUserEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun processIntent(intent: AddUserUiEvent) {
        when (intent) {
            is AddUserUiEvent.UpdateName -> {
                name.value = intent.value
                nameError.value = when {
                    intent.value.isBlank() -> "Name cannot be empty"
                    intent.value.trim().split("\\s+".toRegex()).size != 2 -> "Enter a first and last name"
                    else -> null
                }
            }
            is AddUserUiEvent.UpdateEmail -> {
                email.value = intent.value
                emailError.value = if (!emailRegex.matches(intent.value)) "Invalid email address" else null
            }
            is AddUserUiEvent.UpdateGender -> {
                gender.value = intent.gender
            }
            is AddUserUiEvent.UpdateBirthday -> {
                birthday.value = intent.date
                birthdayError.value = null
                showDatePicker.value = false
            }
            is AddUserUiEvent.ShowDatePicker -> {
                showDatePicker.value = true
            }
            is AddUserUiEvent.HideDatePicker -> {
                showDatePicker.value = false
            }
            is AddUserUiEvent.Submit -> submit()
        }
    }

    private fun submit() {
        nameError.value = when {
            name.value.isBlank() -> "Name cannot be empty"
            name.value.trim().split("\\s+".toRegex()).size != 2 -> "Enter a first and last name"
            else -> null
        }
        emailError.value = if (!emailRegex.matches(email.value)) "Invalid email address" else null
        birthdayError.value = if (birthday.value == null) "Birthday is required" else null
        if (nameError.value != null || emailError.value != null || birthdayError.value != null) return
        viewModelScope.launch {
            isSubmitting.value = true
            createUserUseCase(name.value, email.value, gender.value.name, birthday.value!!)
                .onSuccess {
                    isSuccess.value = true
                    _effects.send(AddUserEffect.NavigateBack)
                    reset()
                }
                .onFailure { e ->
                    isSubmitting.value = false
                    generalError.value = e.message
                }
        }
    }

    fun clearGeneralError() {
        generalError.value = null
    }

    private fun reset() {
        name.value = ""
        email.value = ""
        gender.value = Gender.male
        birthday.value = null
        showDatePicker.value = false
        nameError.value = null
        emailError.value = null
        birthdayError.value = null
        isSubmitting.value = false
        isSuccess.value = false
        generalError.value = null
    }

    companion object {
        private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
