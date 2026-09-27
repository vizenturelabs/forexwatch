package com.vizenture.forexwatch.data.source.local.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vizenture.forexwatch.model.OhlcEntry
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class OhlcEntryListTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String): ImmutableList<OhlcEntry> {
        val listType = object : TypeToken<List<OhlcEntry>>() {}.type
        val list: List<OhlcEntry> = gson.fromJson(value, listType)
        return list.toImmutableList()
    }

    @TypeConverter
    fun fromList(list: ImmutableList<OhlcEntry>): String {
        return gson.toJson(list)
    }
}
