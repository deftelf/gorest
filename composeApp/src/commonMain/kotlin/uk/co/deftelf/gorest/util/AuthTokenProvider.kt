package uk.co.deftelf.gorest.util

expect class AuthTokenProvider() {
    fun getToken(): String
}
