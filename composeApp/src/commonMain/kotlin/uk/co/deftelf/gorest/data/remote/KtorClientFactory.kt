package uk.co.deftelf.gorest.data.remote

import io.ktor.client.HttpClient

expect class KtorClientFactory() {
    fun create(): HttpClient
}
