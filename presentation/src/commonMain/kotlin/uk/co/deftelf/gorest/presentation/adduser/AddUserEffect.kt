package uk.co.deftelf.gorest.presentation.adduser

sealed interface AddUserEffect {
    data object NavigateBack : AddUserEffect
}
