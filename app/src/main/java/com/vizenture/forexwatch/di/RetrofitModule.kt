package com.vizenture.forexwatch.di

import com.vizenture.forexwatch.BuildConfig
import com.vizenture.forexwatch.common.Constants
import com.vizenture.forexwatch.data.source.remote.ForexApi
import com.vizenture.forexwatch.domain.manager.TerminalManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideForexApi(retrofit: Retrofit): ForexApi {
        return retrofit.create(ForexApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(terminalManager: TerminalManager): OkHttpClient {
        val terminalInterceptor = Interceptor { chain ->
            val newRequest = chain.request()
                .newBuilder()
                .header("X-Terminal-Id", terminalManager.getActiveTerminalId())
                .build()

            chain.proceed(newRequest)
        }

        val apiKeyInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val originalUrl = originalRequest.url

            // Alpha Vantage requires the API key as a query parameter, not a header
            val newUrl = originalUrl.newBuilder()
                .addQueryParameter("apikey", BuildConfig.API_KEY)
                .build()

            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            chain.proceed(newRequest)
        }

        val loggingInterceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        return OkHttpClient.Builder()
            .addInterceptor(terminalInterceptor)
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }
}
