package com.vizenture.forexwatch.ui.model

import com.vizenture.forexwatch.model.Percentage
import java.math.BigDecimal

data class ScrubData(
    val price: BigDecimal,
    val timestamp: Long,
    val changePercentage: Percentage,
    val changeAmount: BigDecimal,
)
