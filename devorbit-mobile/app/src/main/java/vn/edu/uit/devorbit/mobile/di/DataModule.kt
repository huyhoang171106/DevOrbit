package vn.edu.uit.devorbit.mobile.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.edu.uit.devorbit.mobile.network.ApiService
import vn.edu.uit.devorbit.mobile.data.remote.interceptor.AuthInterceptor
import vn.edu.uit.devorbit.mobile.data.local.DevOrbitDatabase
import vn.edu.uit.devorbit.mobile.data.local.dao.CourseDao
import vn.edu.uit.devorbit.mobile.data.local.dao.RepoDao
import vn.edu.uit.devorbit.mobile.data.local.dao.RelationshipDao
import vn.edu.uit.devorbit.mobile.data.local.dao.TaskDao
import vn.edu.uit.devorbit.mobile.data.repository.AcademicRepository
import androidx.room.Room
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DevOrbitDatabase {
        return Room.databaseBuilder(
            context,
            DevOrbitDatabase::class.java,
            DevOrbitDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideCourseDao(db: DevOrbitDatabase): CourseDao {
        return db.courseDao()
    }

    @Provides
    @Singleton
    fun provideRepoDao(db: DevOrbitDatabase): RepoDao {
        return db.repoDao()
    }

    @Provides
    @Singleton
    fun provideRelationshipDao(db: DevOrbitDatabase): RelationshipDao {
        return db.relationshipDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(db: DevOrbitDatabase): TaskDao {
        return db.taskDao()
    }

    @Provides
    @Singleton
    fun provideAcademicRepository(
        apiService: ApiService,
        courseDao: CourseDao,
        repoDao: RepoDao,
        relationshipDao: RelationshipDao,
        taskDao: TaskDao
    ): AcademicRepository {
        return AcademicRepository(apiService, courseDao, repoDao, relationshipDao, taskDao)
    }
}
