package com.cheezycode.notesample.di

import com.cheezycode.notesample.api.AuthInterceptor
import com.cheezycode.notesample.api.SearchAPI
import com.cheezycode.notesample.api.UserAPI
import com.cheezycode.notesample.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Singleton
    @Provides
    fun providesUserAPI(retrofitBuilder: Retrofit.Builder): UserAPI {
        return retrofitBuilder.build().create(UserAPI::class.java)
    }
    @Singleton
    @Provides
    fun providesSearchAPI(retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient): SearchAPI {
        return retrofitBuilder.client(okHttpClient).build().create(SearchAPI::class.java)
    }


}