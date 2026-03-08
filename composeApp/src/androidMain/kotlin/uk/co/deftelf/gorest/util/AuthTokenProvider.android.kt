package uk.co.deftelf.gorest.util

import uk.co.deftelf.gorest.BuildConfig

actual class AuthTokenProvider actual constructor() {
    actual fun getToken(): String = BuildConfig.GOREST_API_TOKEN
}
