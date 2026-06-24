package vn.edu.uit.devorbit.mobile.data.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import vn.edu.uit.devorbit.mobile.data.repository.CommunityRepositoryImpl
import vn.edu.uit.devorbit.mobile.domain.repository.CommunityRepository
import vn.edu.uit.devorbit.mobile.network.stomp.StompClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommunityModule {

    @Binds
    @Singleton
    abstract fun bindCommunityRepository(impl: CommunityRepositoryImpl): CommunityRepository
}

@Module
@InstallIn(SingletonComponent::class)
object StompModule {

    @Provides
    @Singleton
    fun provideStompClient(): StompClient {
        return StompClient()
    }
}
