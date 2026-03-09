package uk.co.deftelf.gorest.di

import org.koin.dsl.module
import uk.co.deftelf.gorest.data.connectivity.NetworkMonitor
import uk.co.deftelf.gorest.data.remote.DummyJsonApiService
import uk.co.deftelf.gorest.data.remote.KtorClientFactory

val networkModule = module {
    single { KtorClientFactory().create() }
    single { DummyJsonApiService(get()) }
    single { NetworkMonitor() }
}
