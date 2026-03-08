package uk.co.deftelf.gorest.util

actual class AuthTokenProvider actual constructor() {
    actual fun getToken(): String = System.getenv("GOREST_API_TOKEN") ?: ""
}
