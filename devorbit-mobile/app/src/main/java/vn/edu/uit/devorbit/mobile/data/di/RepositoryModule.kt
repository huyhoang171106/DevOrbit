package vn.edu.uit.devorbit.mobile.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import vn.edu.uit.devorbit.mobile.data.repository.AiRepositoryImpl
import vn.edu.uit.devorbit.mobile.data.repository.AuthRepositoryImpl
import vn.edu.uit.devorbit.mobile.data.repository.BookmarkRepositoryImpl
import vn.edu.uit.devorbit.mobile.data.repository.DiscoveryRepositoryImpl
import vn.edu.uit.devorbit.mobile.data.repository.ResourceRepositoryImpl
import vn.edu.uit.devorbit.mobile.domain.repository.AiRepository
import vn.edu.uit.devorbit.mobile.domain.repository.AuthRepository
import vn.edu.uit.devorbit.mobile.domain.repository.BookmarkRepository
import vn.edu.uit.devorbit.mobile.domain.repository.DiscoveryRepository
import vn.edu.uit.devorbit.mobile.domain.repository.ResourceRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindResourceRepository(impl: ResourceRepositoryImpl): ResourceRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(impl: AiRepositoryImpl): AiRepository

    @Binds
    @Singleton
    abstract fun bindDiscoveryRepository(impl: DiscoveryRepositoryImpl): DiscoveryRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository
}
