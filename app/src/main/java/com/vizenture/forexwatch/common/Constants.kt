package com.vizenture.forexwatch.common

object Constants {
    const val BASE_URL = "https://www.alphavantage.co/"

    const val PARAM_PAIR_SYMBOL = "pairSymbol"
    const val FOREX_DATABASE_NAME = "ForexWatch.db"
    const val MISSING_VALUE_PLACEHOLDER = "—"

    // Hardcoded major pairs to fetch since Alpha Vantage lacks a "list all" endpoint
    val MAJOR_PAIRS = listOf("EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "NZD")
    const val BASE_CURRENCY = "USD"
}
