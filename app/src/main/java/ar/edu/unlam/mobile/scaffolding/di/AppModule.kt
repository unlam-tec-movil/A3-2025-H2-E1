package ar.edu.unlam.mobile.scaffolding.di

import ar.edu.unlam.mobile.scaffolding.data.datasources.network.GraphHopperApi
import ar.edu.unlam.mobile.scaffolding.data.repositories.EventRepositoryImpl
import ar.edu.unlam.mobile.scaffolding.data.repositories.NavigationRepositoryImpl
import ar.edu.unlam.mobile.scaffolding.data.repositories.UserRepositoryImpl
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import ar.edu.unlam.mobile.scaffolding.domain.navigation.repositories.NavigationRepository
import ar.edu.unlam.mobile.scaffolding.domain.user.repositories.AuthRepository
import ar.edu.unlam.mobile.scaffolding.domain.user.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideGraphHopperApi(): GraphHopperApi =
        Retrofit
            .Builder()
            .baseUrl(GraphHopperApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GraphHopperApi::class.java)

    @Provides
    @Singleton
    fun provideEventRepository(): EventRepository = EventRepositoryImpl()

    @Provides
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl

    @Provides
    fun provideAuthRepository(impl: UserRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideNavigationRepository(api: GraphHopperApi): NavigationRepository =
        NavigationRepositoryImpl(
            api,
        )
}
