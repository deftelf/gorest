package uk.co.deftelf.gorest.presentation.userfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.co.deftelf.gorest.domain.model.User
import uk.co.deftelf.gorest.domain.usecase.DeleteUserUseCase
import uk.co.deftelf.gorest.domain.usecase.GetUsersUseCase

class UserFeedViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
) : ViewModel() {

    private val users = MutableStateFlow<List<User>>(emptyList())
    private val isLoading = MutableStateFlow(true)
    private val error = MutableStateFlow<String?>(null)
    private val pendingDeleteId = MutableStateFlow<Long?>(null)

    val state: StateFlow<UserFeedUiState> = combine(
        users, isLoading, error, pendingDeleteId,
    ) { users, isLoading, error, pendingDeleteId ->
        UserFeedUiState(
            users = users,
            isLoading = isLoading,
            error = error,
            pendingDeleteId = pendingDeleteId,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UserFeedUiState())

    private val _effects = Channel<UserFeedEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val pendingDeletes = mutableMapOf<Long, User>()

    init {
        getUsersUseCase()
            .onEach { fetchedUsers ->
                users.value = fetchedUsers
                isLoading.value = false
            }
            .launchIn(viewModelScope)
        processIntent(UserFeedUiEvent.LoadUsers)
    }

    fun processIntent(intent: UserFeedUiEvent) {
        when (intent) {
            is UserFeedUiEvent.LoadUsers -> refresh()
            is UserFeedUiEvent.Refresh -> refresh()
            is UserFeedUiEvent.RequestDelete -> {
                pendingDeleteId.value = intent.userId
            }
            is UserFeedUiEvent.ConfirmDelete -> confirmDelete(intent.userId)
            is UserFeedUiEvent.UndoDelete -> undoDelete(intent.userId)
            is UserFeedUiEvent.CommitDelete -> commitDelete(intent.userId)
            is UserFeedUiEvent.DismissError -> {
                error.value = null
                pendingDeleteId.value = null
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            getUsersUseCase.refresh().onFailure { e ->
                error.value = e.message ?: "Unknown error"
                isLoading.value = false
            }
        }
    }

    private fun confirmDelete(userId: Long) {
        pendingDeleteId.value = null
        val user = users.value.find { it.id == userId } ?: return
        pendingDeletes[userId] = user
        users.value = users.value.filter { it.id != userId }
        viewModelScope.launch {
            _effects.send(UserFeedEffect.ShowUndoSnackbar(userId, user.name))
            delay(5_000)
            processIntent(UserFeedUiEvent.CommitDelete(userId))
        }
    }

    private fun undoDelete(userId: Long) {
        val user = pendingDeletes.remove(userId) ?: return
        users.value = (users.value + user).sortedByDescending { it.id }
    }

    private fun commitDelete(userId: Long) {
        val user = pendingDeletes.remove(userId) ?: return
        viewModelScope.launch {
            deleteUserUseCase(userId).onFailure { e ->
                users.value = (users.value + user).sortedByDescending { it.id }
                _effects.send(UserFeedEffect.ShowError(e.message ?: "Delete failed"))
                getUsersUseCase.refresh()
            }
        }
    }
}
