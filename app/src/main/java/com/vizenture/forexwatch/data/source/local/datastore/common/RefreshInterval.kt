package com.vizenture.forexwatch.data.source.local.datastore.common

// FIX: Added @Suppress("unused") to silence IDE warnings.
// These enum values will be used in Settings UI and repository cache logic.
@Suppress("unused")
enum class RefreshInterval(val minutes: Int, val displayName: String) {
    MIN_15(15, "15 Minutes"),
    MIN_30(30, "30 Minutes"),
    MIN_60(60, "1 Hour"),
    MIN_120(120, "2 Hours");

    val millis: Long get() = minutes * 60 * 1000L
}
