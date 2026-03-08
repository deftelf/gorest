package uk.co.deftelf.gorest

import androidx.compose.runtime.Composable
import org.koin.compose.KoinContext
import uk.co.deftelf.gorest.ui.navigation.AppNavigation
import uk.co.deftelf.gorest.ui.theme.GoRestTheme

@Composable
fun App() {
    KoinContext {
        GoRestTheme {
            AppNavigation()
        }
    }
}
