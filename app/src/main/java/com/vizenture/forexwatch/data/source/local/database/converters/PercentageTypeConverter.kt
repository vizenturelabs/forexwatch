package com.vizenture.forexwatch.data.source.local.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.vizenture.forexwatch.model.Percentage

class PercentageTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromPercentage(percentage: Percentage): String {
        return gson.toJson(percentage)
    }

    @TypeConverter
    fun toPercentage(percentageJson: String): Percentage {
        return gson.fromJson(percentageJson, Percentage::class.java)
    }
}
