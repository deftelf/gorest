package uk.co.deftelf.gorest.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object UserList : Destination
    @Serializable
    data object AddUser : Destination
    @Serializable
    data class UserDetail(val userId: Long) : Destination
}
