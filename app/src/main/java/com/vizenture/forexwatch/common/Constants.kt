package com.vizenture.forexwatch.common

import java.util.Locale

object Constants {
    const val BASE_URL = "https://api.twelvedata.com/"

    private const val TERMINAL_BRIDGE_URL_TEMPLATE = "http://10.0.2.2:808%d/"

    fun getBaseUrl(terminalId: Int, useLocalBridge: Boolean = false): String {
        return if (useLocalBridge && terminalId > 0) {
            // FIX: Added Locale.US to prevent formatting bugs in non-US regions
            String.format(Locale.US, TERMINAL_BRIDGE_URL_TEMPLATE, terminalId)
        } else {
            BASE_URL
        }
    }

    const val PARAM_PAIR_SYMBOL = "pairSymbol"
    const val FOREX_DATABASE_NAME = "ForexWatch.db"
    const val MISSING_VALUE_PLACEHOLDER = "—"

    val MAJOR_PAIRS = listOf("EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "NZD")
    const val BASE_CURRENCY = "USD"

    // Helper to format symbols correctly for Twelve Data
    fun getStandardForexSymbol(from: String, to: String): String {
        val baseCurrencies = listOf("EUR", "GBP", "AUD", "NZD")
        val quoteCurrencies = listOf("JPY", "CAD", "CHF")

        // FIX: Introduced 'from' as the subject of the 'when' expression
        return when (from) {
            in baseCurrencies -> "$from/$to" // e.g., EUR/USD, GBP/USD
            in quoteCurrencies -> if (to == "USD") "$to/$from" else "$from/$to" // e.g., USD/JPY (reversed)
            else -> "$from/$to" // e.g., USD/CAD, USD/CHF, or fallback
        }
    }
}
