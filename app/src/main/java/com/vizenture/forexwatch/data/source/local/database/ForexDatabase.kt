package com.vizenture.forexwatch.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vizenture.forexwatch.data.source.local.database.converters.ImmutableListTypeConverter
import com.vizenture.forexwatch.data.source.local.database.converters.OhlcEntryListTypeConverter
import com.vizenture.forexwatch.data.source.local.database.converters.PercentageTypeConverter
import com.vizenture.forexwatch.data.source.local.database.converters.PriceTypeConverter
import com.vizenture.forexwatch.data.source.local.database.dao.CachedChartDao
import com.vizenture.forexwatch.data.source.local.database.dao.CurrencyPairDao
import com.vizenture.forexwatch.data.source.local.database.dao.FavouriteCurrencyPairDao
import com.vizenture.forexwatch.data.source.local.database.dao.FavouriteSymbolDao
import com.vizenture.forexwatch.data.source.local.database.model.CachedChart
import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteSymbol

@Database(
    version = 2, // Incremented version to trigger destructive migration
    entities = [
        CurrencyPair::class,
        FavouriteCurrencyPair::class,
        FavouriteSymbol::class,
        CachedChart::class // Added new entity for chart caching
    ],
    exportSchema = true
)
@TypeConverters(
    PriceTypeConverter::class,
    PercentageTypeConverter::class,
    ImmutableListTypeConverter::class,
    OhlcEntryListTypeConverter::class,
    ChartTypeConverters::class // Added new converter for CachedChart's List<OhlcEntry>
)
abstract class ForexDatabase : RoomDatabase() {
    abstract fun currencyPairDao(): CurrencyPairDao
    abstract fun favouriteCurrencyPairDao(): FavouriteCurrencyPairDao
    abstract fun favouriteSymbolDao(): FavouriteSymbolDao

    // FIX: Added @Suppress("unused") to silence the IDE warning.
    // This DAO is accessed via dependency injection, so it appears unused to the compiler.
    @Suppress("unused")
    abstract fun cachedChartDao(): CachedChartDao
}
