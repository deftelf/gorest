package uk.co.deftelf.gorest.presentation.adduser

import kotlinx.datetime.LocalDate
import uk.co.deftelf.gorest.domain.model.Gender

sealed interface AddUserIntent {
    data class UpdateName(val value: String) : AddUserIntent
    data class UpdateEmail(val value: String) : AddUserIntent
    data class UpdateGender(val gender: Gender) : AddUserIntent
    data class UpdateBirthday(val date: LocalDate) : AddUserIntent
    data object ShowDatePicker : AddUserIntent
    data object HideDatePicker : AddUserIntent
    data object Submit : AddUserIntent
}
