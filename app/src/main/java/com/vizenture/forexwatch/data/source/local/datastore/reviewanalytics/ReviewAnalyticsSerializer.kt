package com.vizenture.forexwatch.data.source.local.datastore.reviewanalytics

import com.vizenture.forexwatch.data.source.local.datastore.BasePreferencesSerializer

object ReviewAnalyticsSerializer : BasePreferencesSerializer<ReviewAnalytics>(
    defaultInstance = { ReviewAnalytics() },
    serializer = ReviewAnalytics.serializer(),
)
