package uk.co.deftelf.gorest.presentation.userfeed

sealed interface UserFeedIntent {
    data object LoadUsers : UserFeedIntent
    data object Refresh : UserFeedIntent
    data class RequestDelete(val userId: Long) : UserFeedIntent
    data class ConfirmDelete(val userId: Long) : UserFeedIntent
    data class UndoDelete(val userId: Long) : UserFeedIntent
    data class CommitDelete(val userId: Long) : UserFeedIntent
    data class SelectUser(val userId: Long?) : UserFeedIntent
    data object DismissError : UserFeedIntent
}
