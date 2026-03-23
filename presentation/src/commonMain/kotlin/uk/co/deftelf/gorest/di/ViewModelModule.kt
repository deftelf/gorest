package uk.co.deftelf.gorest.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import uk.co.deftelf.gorest.domain.usecase.CreateUserUseCase
import uk.co.deftelf.gorest.domain.usecase.DeleteUserUseCase
import uk.co.deftelf.gorest.domain.usecase.GetUsersUseCase
import uk.co.deftelf.gorest.presentation.adduser.AddUserViewModel
import uk.co.deftelf.gorest.presentation.userfeed.UserFeedViewModel

val viewModelModule = module {
    single { GetUsersUseCase(get()) }
    single { CreateUserUseCase(get()) }
    single { DeleteUserUseCase(get()) }
    viewModel { UserFeedViewModel(get(), get()) }
    viewModel { AddUserViewModel(get()) }
}
