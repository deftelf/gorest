package uk.co.deftelf.gorest

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin
import uk.co.deftelf.gorest.di.appModule

fun main() {
    startKoin {
        modules(appModule)
    }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "GoRest",
        ) {
            App()
        }
    }
}