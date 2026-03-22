package uk.co.deftelf.gorest.presentation.adduser

import kotlinx.datetime.LocalDate
import uk.co.deftelf.gorest.domain.model.Gender

sealed interface AddUserUiEvent {
    data class UpdateName(val value: String) : AddUserUiEvent
    data class UpdateEmail(val value: String) : AddUserUiEvent
    data class UpdateGender(val gender: Gender) : AddUserUiEvent
    data class UpdateBirthday(val date: LocalDate) : AddUserUiEvent
    data object ShowDatePicker : AddUserUiEvent
    data object HideDatePicker : AddUserUiEvent
    data object Submit : AddUserUiEvent
}
