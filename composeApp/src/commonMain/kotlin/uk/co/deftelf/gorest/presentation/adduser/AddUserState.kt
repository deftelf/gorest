package uk.co.deftelf.gorest.presentation.adduser

import uk.co.deftelf.gorest.domain.model.Gender

data class AddUserState(
    val name: String = "",
    val email: String = "",
    val gender: Gender = Gender.male,
    val nameError: String? = null,
    val emailError: String? = null,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val generalError: String? = null,
)
