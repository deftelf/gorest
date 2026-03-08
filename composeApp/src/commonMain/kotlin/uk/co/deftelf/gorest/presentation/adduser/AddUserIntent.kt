package uk.co.deftelf.gorest.presentation.adduser

import uk.co.deftelf.gorest.domain.model.Gender
import uk.co.deftelf.gorest.domain.model.UserStatus

sealed interface AddUserIntent {
    data class UpdateName(val value: String) : AddUserIntent
    data class UpdateEmail(val value: String) : AddUserIntent
    data class UpdateGender(val gender: Gender) : AddUserIntent
    data class UpdateStatus(val status: UserStatus) : AddUserIntent
    data object Submit : AddUserIntent
}
