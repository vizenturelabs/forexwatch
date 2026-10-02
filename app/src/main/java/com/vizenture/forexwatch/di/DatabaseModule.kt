package com.vizenture.forexwatch.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.vizenture.forexwatch.common.Constants
import com.vizenture.forexwatch.data.source.local.database.ForexDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideForexDatabase(@ApplicationContext context: Context): ForexDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ForexDatabase::class.java,
            Constants.FOREX_DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
