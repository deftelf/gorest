package uk.co.deftelf.gorest.di

import org.koin.dsl.module
import uk.co.deftelf.gorest.data.local.DatabaseDriverFactory
import uk.co.deftelf.gorest.data.local.GoRestDatabase

val databaseModule = module {
    single { DatabaseDriverFactory().createDriver() }
    single { GoRestDatabase(get()) }
}
