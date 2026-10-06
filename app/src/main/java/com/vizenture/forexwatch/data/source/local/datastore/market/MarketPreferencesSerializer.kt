package com.vizenture.forexwatch.data.source.local.datastore.market

import com.vizenture.forexwatch.data.source.local.datastore.BasePreferencesSerializer

object MarketPreferencesSerializer : BasePreferencesSerializer<MarketPreferences>(
    defaultInstance = { MarketPreferences() },
    serializer = MarketPreferences.serializer()
)
