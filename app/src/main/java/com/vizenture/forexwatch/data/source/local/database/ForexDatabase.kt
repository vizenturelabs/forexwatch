package com.vizenture.forexwatch.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vizenture.forexwatch.data.source.local.database.converters.ImmutableListTypeConverter
/* import com.vizenture.forexwatch.data.source.local.database.converters.OhlcEntryListTypeConverter */
import com.vizenture.forexwatch.data.source.local.database.converters.PercentageTypeConverter
import com.vizenture.forexwatch.data.source.local.database.converters.PriceTypeConverter
/* import com.vizenture.forexwatch.data.source.local.database.dao.CurrencyPairDao */
/* import com.vizenture.forexwatch.data.source.local.database.dao.FavouriteCurrencyPairDao */
/* import com.vizenture.forexwatch.data.source.local.database.dao.FavouriteSymbolDao */
import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteSymbol

@Database(
    version = 1,
    entities = [CurrencyPair::class, FavouriteCurrencyPair::class, FavouriteSymbol::class],
    exportSchema = true
)
@TypeConverters(
    PriceTypeConverter::class,
    PercentageTypeConverter::class,
    ImmutableListTypeConverter::class,
    OhlcEntryListTypeConverter::class
)
abstract class ForexDatabase : RoomDatabase() {
    abstract fun currencyPairDao(): CurrencyPairDao
    abstract fun favouriteCurrencyPairDao(): FavouriteCurrencyPairDao
    abstract fun favouriteSymbolDao(): FavouriteSymbolDao
}
