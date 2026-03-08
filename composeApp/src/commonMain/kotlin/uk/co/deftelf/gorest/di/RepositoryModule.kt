package uk.co.deftelf.gorest.di

import org.koin.dsl.module
import uk.co.deftelf.gorest.data.repository.UserRepositoryDummyImpl
import uk.co.deftelf.gorest.domain.repository.UserRepository

val repositoryModule = module {
    single<UserRepository> {
        //UserRepositoryImpl(get(), get(), get())
        UserRepositoryDummyImpl()
    }
}
