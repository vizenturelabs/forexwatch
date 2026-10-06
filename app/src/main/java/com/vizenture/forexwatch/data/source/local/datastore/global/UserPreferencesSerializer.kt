package com.vizenture.forexwatch.data.source.local.datastore.global

import com.vizenture.forexwatch.data.source.local.datastore.BasePreferencesSerializer

object UserPreferencesSerializer : BasePreferencesSerializer<UserPreferences>(
    defaultInstance = { UserPreferences() },
    serializer = UserPreferences.serializer(),
)
