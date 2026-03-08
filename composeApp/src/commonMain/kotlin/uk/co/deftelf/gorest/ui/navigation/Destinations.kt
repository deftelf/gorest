package uk.co.deftelf.gorest.ui.navigation

sealed interface Destination {
    data object Feed : Destination
    data object AddUser : Destination
}
