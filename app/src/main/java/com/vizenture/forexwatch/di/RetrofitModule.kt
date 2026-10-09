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
    fun provideForexApi(retrofit: Retrofit): ForexApi {
        return retrofit.create(ForexApi::class.java)
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, terminalManager: TerminalManager): Retrofit {
        return Retrofit.Builder()
            // Defaults to public API (useLocalBridge = false) so the app actually fetches data
            .baseUrl(Constants.getBaseUrl(terminalManager.getActiveTerminalId().toInt(), useLocalBridge = false))
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
            val newUrl = originalRequest.url.newBuilder()
                .addQueryParameter("apikey", BuildConfig.API_KEY)
                .build()
            val newRequest = originalRequest.newBuilder().url(newUrl).build()
            chain.proceed(newRequest)
        }

        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        else loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)

        return OkHttpClient.Builder()
            .addInterceptor(terminalInterceptor)
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }
}
