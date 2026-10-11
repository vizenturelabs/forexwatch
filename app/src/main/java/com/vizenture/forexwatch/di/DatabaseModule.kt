package com.vizenture.forexwatch.di

import android.content.Context
import androidx.room.Room
import com.vizenture.forexwatch.common.Constants
import com.vizenture.forexwatch.data.source.local.database.ForexDatabase
import com.vizenture.forexwatch.data.source.local.database.dao.CachedChartDao
import com.vizenture.forexwatch.data.source.local.database.dao.CurrencyPairDao
import com.vizenture.forexwatch.data.source.local.database.dao.FavouriteCurrencyPairDao
import com.vizenture.forexwatch.data.source.local.database.dao.FavouriteSymbolDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
            // FIX: Added 'true' to explicitly confirm dropping all tables on destructive migration
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideCurrencyPairDao(database: ForexDatabase): CurrencyPairDao {
        return database.currencyPairDao()
    }

    @Provides
    @Singleton
    fun provideFavouriteCurrencyPairDao(database: ForexDatabase): FavouriteCurrencyPairDao {
        return database.favouriteCurrencyPairDao()
    }

    @Provides
    @Singleton
    fun provideFavouriteSymbolDao(database: ForexDatabase): FavouriteSymbolDao {
        return database.favouriteSymbolDao()
    }

    // FIX: Added @Suppress("unused") to silence the IDE warning.
    // Hilt uses this method via annotation processing, so it appears unused to the compiler.
    @Suppress("unused")
    @Provides
    @Singleton
    fun provideCachedChartDao(database: ForexDatabase): CachedChartDao {
        return database.cachedChartDao()
    }
}
