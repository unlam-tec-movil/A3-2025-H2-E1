package ar.edu.unlam.mobile.scaffolding.di

import ar.edu.unlam.mobile.scaffolding.data.repositories.UserRepositoryImpl
import ar.edu.unlam.mobile.scaffolding.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    @Binds
    abstract fun bindLogin(
        impl: UserRepositoryImpl
    ): UserRepository
}