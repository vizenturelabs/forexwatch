package com.vizenture.forexwatch.model

import java.math.BigDecimal

data class OhlcEntry(
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val timestamp: Long
)
