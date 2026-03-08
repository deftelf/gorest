package uk.co.deftelf.gorest

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "GoRest",
    ) {
        App()
    }
}