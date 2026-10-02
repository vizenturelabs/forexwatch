package com.vizenture.forexwatch.model

import com.vizenture.forexwatch.common.toSanitisedBigDecimalOrNull
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import java.math.BigDecimal
import java.util.Locale

data class Price(
    val price: String?, 
    val currency: Currency = Currency.USD,
    val isJpyPair: Boolean = false
) : Comparable<Price> {

    val amount: BigDecimal
        get() = price.toSanitisedBigDecimalOrNull() ?: BigDecimal.ZERO

    val formattedAmount: String
        get() = formatAmount()

    private fun formatAmount(): String {
        val formatLocale = Locale.getDefault(Locale.Category.FORMAT)
        val amountToFormat = price.toSanitisedBigDecimalOrNull()

        return if (amountToFormat == null) {
            PriceFormatter.formatMissing(currency = currency, formatLocale = formatLocale)
        } else {
            PriceFormatter.format(
                amount = amountToFormat,
                currency = currency,
                formatLocale = formatLocale,
                isJpyPair = isJpyPair
            )
        }
    }

    override fun compareTo(other: Price) = amount.compareTo(other.amount)
}
