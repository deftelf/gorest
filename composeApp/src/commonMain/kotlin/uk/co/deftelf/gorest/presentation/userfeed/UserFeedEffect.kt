package uk.co.deftelf.gorest.presentation.userfeed

sealed interface UserFeedEffect {
    data class ShowUndoSnackbar(val userId: Long, val userName: String) : UserFeedEffect
    data class ShowError(val message: String) : UserFeedEffect
}
