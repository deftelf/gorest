package uk.co.deftelf.gorest.presentation.userfeed

sealed interface UserFeedUiEvent {
    data object LoadUsers : UserFeedUiEvent
    data object Refresh : UserFeedUiEvent
    data class RequestDelete(val userId: Long) : UserFeedUiEvent
    data class ConfirmDelete(val userId: Long) : UserFeedUiEvent
    data class UndoDelete(val userId: Long) : UserFeedUiEvent
    data class CommitDelete(val userId: Long) : UserFeedUiEvent
    data object DismissError : UserFeedUiEvent
}
