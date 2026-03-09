package uk.co.deftelf.gorest.presentation.userfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.co.deftelf.gorest.domain.model.User
import uk.co.deftelf.gorest.domain.usecase.DeleteUserUseCase
import uk.co.deftelf.gorest.domain.usecase.GetUsersUseCase

class UserFeedViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(UserFeedState())
    val state: StateFlow<UserFeedState> = _state.asStateFlow()

    private val _effects = Channel<UserFeedEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val pendingDeletes = mutableMapOf<Long, User>()

    init {
        getUsersUseCase()
            .onEach { users ->
                _state.update { it.copy(users = users, isLoading = false) }
            }
            .launchIn(viewModelScope)
        processIntent(UserFeedIntent.LoadUsers)
    }

    fun processIntent(intent: UserFeedIntent) {
        when (intent) {
            is UserFeedIntent.LoadUsers -> refresh()
            is UserFeedIntent.Refresh -> refresh()
            is UserFeedIntent.RequestDelete -> {
                _state.update { it.copy(pendingDeleteId = intent.userId) }
            }
            is UserFeedIntent.ConfirmDelete -> confirmDelete(intent.userId)
            is UserFeedIntent.UndoDelete -> undoDelete(intent.userId)
            is UserFeedIntent.CommitDelete -> commitDelete(intent.userId)
            is UserFeedIntent.DismissError -> {
                _state.update { it.copy(error = null, pendingDeleteId = null) }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getUsersUseCase.refresh().onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Unknown error", isLoading = false) }
            }
        }
    }

    private fun confirmDelete(userId: Long) {
        _state.update { it.copy(pendingDeleteId = null) }
        val user = _state.value.users.find { it.id == userId } ?: return
        pendingDeletes[userId] = user
        _state.update { current ->
            current.copy(users = current.users.filter { it.id != userId })
        }
        viewModelScope.launch {
            _effects.send(UserFeedEffect.ShowUndoSnackbar(userId, user.name))
            delay(5_000)
            processIntent(UserFeedIntent.CommitDelete(userId))
        }
    }

    private fun undoDelete(userId: Long) {
        val user = pendingDeletes.remove(userId) ?: return
        _state.update { current ->
            val updated = (current.users + user).sortedByDescending { it.id }
            current.copy(users = updated)
        }
    }

    private fun commitDelete(userId: Long) {
        val user = pendingDeletes.remove(userId) ?: return
        viewModelScope.launch {
            deleteUserUseCase(userId).onFailure { e ->
                // Restore user on failure
                _state.update { current ->
                    val updated = (current.users + user).sortedByDescending { it.id }
                    current.copy(users = updated)
                }
                _effects.send(UserFeedEffect.ShowError(e.message ?: "Delete failed"))
                getUsersUseCase.refresh()
            }
        }
    }
}
