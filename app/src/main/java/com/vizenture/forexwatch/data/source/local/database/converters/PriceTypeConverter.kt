package com.vizenture.forexwatch.data.source.local.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.vizenture.forexwatch.model.Price

class PriceTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromPrice(price: Price): String {
        return gson.toJson(price)
    }

    @TypeConverter
    fun toPrice(priceJson: String): Price {
        return gson.fromJson(priceJson, Price::class.java)
    }
}
