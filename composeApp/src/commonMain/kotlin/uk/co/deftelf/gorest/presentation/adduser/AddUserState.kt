package uk.co.deftelf.gorest.presentation.adduser

import uk.co.deftelf.gorest.domain.model.Gender
import uk.co.deftelf.gorest.domain.model.UserStatus

data class AddUserState(
    val name: String = "",
    val email: String = "",
    val gender: Gender = Gender.male,
    val status: UserStatus = UserStatus.active,
    val nameError: String? = null,
    val emailError: String? = null,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
)
