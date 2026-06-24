package vn.edu.uit.devorbit.admin.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.edu.uit.devorbit.admin.BuildConfig
import vn.edu.uit.devorbit.admin.data.remote.interceptor.AuthInterceptor
import vn.edu.uit.devorbit.admin.data.repository.AdminRepositoryImpl
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import vn.edu.uit.devorbit.admin.data.remote.interceptor.RetryInterceptor
import vn.edu.uit.devorbit.admin.network.AdminApiService
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdminModule {

    companion object {
        private const val BASE_URL = BuildConfig.API_BASE_URL

        @Provides
        @Singleton
        fun provideLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE
            }
        }

        @Provides
        @Singleton
        fun provideRetryInterceptor(): RetryInterceptor = RetryInterceptor()

        @Provides
        @Singleton
        fun provideOkHttpClient(
            loggingInterceptor: HttpLoggingInterceptor,
            authInterceptor: AuthInterceptor,
            retryInterceptor: RetryInterceptor
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(retryInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
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
        fun provideAdminApiService(retrofit: Retrofit): AdminApiService {
            return retrofit.create(AdminApiService::class.java)
        }

        @Provides
        @Singleton
        fun provideAdminRepository(impl: AdminRepositoryImpl): AdminRepository = impl
    }
}
