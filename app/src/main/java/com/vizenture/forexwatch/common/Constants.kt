package com.vizenture.forexwatch.common

object Constants {
    const val BASE_URL = "https://www.alphavantage.co/"

    // 10.0.2.2 is the special alias for the host machine's localhost in the Android Emulator.
    // 127.0.0.1 on an emulator/device refers to the device itself, not the host machine running the MT4 terminals.
    private const val TERMINAL_BRIDGE_URL_TEMPLATE = "http://10.0.2.2:808%d/"

    // Default to false so the app fetches real data out-of-the-box until your local bridge infrastructure is active.
    fun getBaseUrl(terminalId: Int, useLocalBridge: Boolean = false): String {
        return if (useLocalBridge && terminalId > 0) {
            String.format(TERMINAL_BRIDGE_URL_TEMPLATE, terminalId)
        } else {
            BASE_URL
        }
    }

    const val PARAM_PAIR_SYMBOL = "pairSymbol"
    const val FOREX_DATABASE_NAME = "ForexWatch.db"
    const val MISSING_VALUE_PLACEHOLDER = "—"

    val MAJOR_PAIRS = listOf("EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "NZD")
    const val BASE_CURRENCY = "USD"
}
