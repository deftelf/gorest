package uk.co.deftelf.gorest

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import uk.co.deftelf.gorest.di.appModule

fun MainViewController() = ComposeUIViewController {
    if (GlobalContext.getOrNull() == null) {
        startKoin {
            modules(appModule)
        }
    }
    App()
}
