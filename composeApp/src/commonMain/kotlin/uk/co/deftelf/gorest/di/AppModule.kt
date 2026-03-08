package uk.co.deftelf.gorest.di

import org.koin.dsl.module

val appModule = module {
    includes(networkModule, databaseModule, repositoryModule, viewModelModule)
}
