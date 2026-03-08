package uk.co.deftelf.gorest.di

import org.koin.dsl.module
import uk.co.deftelf.gorest.data.remote.GoRestApiService
import uk.co.deftelf.gorest.data.remote.KtorClientFactory
import uk.co.deftelf.gorest.util.AuthTokenProvider

val networkModule = module {
    single { KtorClientFactory().create() }
    single { GoRestApiService(get()) }
    single { AuthTokenProvider() }
}
