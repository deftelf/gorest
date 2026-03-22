package uk.co.deftelf.gorest.presentation.userfeed

import uk.co.deftelf.gorest.domain.model.User

data class UserFeedUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val pendingDeleteId: Long? = null,
)
