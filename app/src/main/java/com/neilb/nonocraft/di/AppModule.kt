package com.neilb.nonocraft.di

import android.app.Application
import androidx.room.Room
import com.neilb.nonocraft.common.Constants
import com.neilb.nonocraft.data.remote.AuthInterceptor
import com.neilb.nonocraft.data.remote.service.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(Constants.API_URL)
            .build()
    }

    @Provides @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides @Singleton
    fun providePuzzleApiService(retrofit: Retrofit): PuzzleApiService =
        retrofit.create(PuzzleApiService::class.java)

    @Provides @Singleton
    fun provideSocialApiService(retrofit: Retrofit): SocialApiService =
        retrofit.create(SocialApiService::class.java)

    @Provides @Singleton
    fun providePlaylistApiService(retrofit: Retrofit): PlaylistApiService =
        retrofit.create(PlaylistApiService::class.java)

    @Provides @Singleton
    fun provideFeedApiService(retrofit: Retrofit): FeedApiService =
        retrofit.create(FeedApiService::class.java)

    @Provides @Singleton
    fun provideDailyApiService(retrofit: Retrofit): DailyApiService =
        retrofit.create(DailyApiService::class.java)

    @Provides @Singleton
    fun provideRankingApiService(retrofit: Retrofit): RankingApiService =
        retrofit.create(RankingApiService::class.java)

    @Provides @Singleton
    fun providePremiumApiService(retrofit: Retrofit): PremiumApiService =
        retrofit.create(PremiumApiService::class.java)
}
