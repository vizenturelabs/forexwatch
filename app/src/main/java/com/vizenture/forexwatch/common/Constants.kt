package com.vizenture.forexwatch.common

object Constants {
    const val DEFAULT_PUBLIC_URL = "https://www.alphavantage.co/"

    // Local bridge template for multi-terminal routing (e.g., ports 8081, 8082)
    private const val TERMINAL_BRIDGE_URL_TEMPLATE = "http://127.0.0.1:808%d/"

    fun getBaseUrl(terminalId: Int): String {
        return if (terminalId > 0) {
            String.format(TERMINAL_BRIDGE_URL_TEMPLATE, terminalId)
        } else {
            DEFAULT_PUBLIC_URL
        }
    }

    const val PARAM_PAIR_SYMBOL = "pairSymbol"
    const val FOREX_DATABASE_NAME = "ForexWatch.db"
    const val MISSING_VALUE_PLACEHOLDER = "—"

    val MAJOR_PAIRS = listOf("EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "NZD")
    const val BASE_CURRENCY = "USD"
}
