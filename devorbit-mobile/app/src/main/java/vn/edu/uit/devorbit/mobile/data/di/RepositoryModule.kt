package vn.edu.uit.devorbit.mobile.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import vn.edu.uit.devorbit.mobile.data.repository.AuthRepositoryImpl
import vn.edu.uit.devorbit.mobile.data.repository.BookmarkRepositoryImpl
import vn.edu.uit.devorbit.mobile.data.repository.DiscoveryRepositoryImpl
import vn.edu.uit.devorbit.mobile.data.repository.StudyPlanRepositoryImpl
import vn.edu.uit.devorbit.mobile.data.repository.SubjectQaRepositoryImpl
import vn.edu.uit.devorbit.mobile.domain.repository.AuthRepository
import vn.edu.uit.devorbit.mobile.domain.repository.BookmarkRepository
import vn.edu.uit.devorbit.mobile.domain.repository.DiscoveryRepository
import vn.edu.uit.devorbit.mobile.domain.repository.StudyPlanRepository
import vn.edu.uit.devorbit.mobile.domain.repository.SubjectQaRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    
    
    @Binds
    @Singleton
    abstract fun bindDiscoveryRepository(impl: DiscoveryRepositoryImpl): DiscoveryRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository

    @Binds
    @Singleton
    abstract fun bindStudyPlanRepository(impl: StudyPlanRepositoryImpl): StudyPlanRepository

    @Binds
    @Singleton
    abstract fun bindSubjectQaRepository(impl: SubjectQaRepositoryImpl): SubjectQaRepository
}
