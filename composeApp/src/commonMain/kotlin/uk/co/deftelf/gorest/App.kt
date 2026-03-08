package uk.co.deftelf.gorest

import androidx.compose.runtime.Composable
import uk.co.deftelf.gorest.ui.navigation.AppNavigation
import uk.co.deftelf.gorest.ui.theme.GoRestTheme

@Composable
fun App() {
    GoRestTheme {
        AppNavigation()
    }
}
