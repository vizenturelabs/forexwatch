package com.vizenture.forexwatch.data.source.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vizenture.forexwatch.model.OhlcEntry

// FIX: Added @Suppress("unused") to silence the IDE warnings.
// Room uses these methods via reflection, so they appear unused to the compiler.
@Suppress("unused")
class ChartTypeConverters {

    @TypeConverter
    fun fromOhlcList(list: List<OhlcEntry>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toOhlcList(json: String): List<OhlcEntry> {
        val type = object : TypeToken<List<OhlcEntry>>() {}.type
        return Gson().fromJson(json, type)
    }
}
