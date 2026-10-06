package com.vizenture.forexwatch.data.source.local.datastore.favourites

import com.vizenture.forexwatch.data.source.local.datastore.BasePreferencesSerializer

object FavouritesPreferencesSerializer : BasePreferencesSerializer<FavouritesPreferences>(
    defaultInstance = { FavouritesPreferences() },
    serializer = FavouritesPreferences.serializer(),
)
