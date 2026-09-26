package com.vizenture.forexwatch.model

import com.vizenture.forexwatch.common.Constants.MISSING_VALUE_PLACEHOLDER
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import java.math.BigDecimal
import java.text.AttributedCharacterIterator
import java.text.CharacterIterator
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import java.util.Currency as JavaCurrency

internal object PriceFormatter {
    
    fun format(
        amount: BigDecimal, 
        currency: Currency, 
        formatLocale: Locale, 
        isJpyPair: Boolean = false
    ): String {
        val decimalPlaces = if (isJpyPair) 3 else 5
        
        val numberFormat = createNumberFormat(
            formatLocale = formatLocale,
            decimalPlaces = decimalPlaces
        )

        return numberFormat.format(amount)
    }

    fun formatMissing(currency: Currency, formatLocale: Locale): String {
        val numberFormat = createNumberFormat(
            formatLocale = formatLocale,
            decimalPlaces = 5
        )
        return withNumericSpan(numberFormat, BigDecimal.ZERO) { formatted, start, end ->
            formatted.substring(0, start) + MISSING_VALUE_PLACEHOLDER + formatted.substring(end)
        } ?: defaultMissingFallback(currency, formatLocale)
    }

    private fun createNumberFormat(
        formatLocale: Locale,
        decimalPlaces: Int,
    ): DecimalFormat {
        // Forex exchange rates are ratios (e.g., 1.08452) rather than absolute fiat values,
        // so getNumberInstance is used instead of getCurrencyInstance to avoid currency symbols.
        val numberFormat = NumberFormat.getNumberInstance(formatLocale) as DecimalFormat

        numberFormat.minimumFractionDigits = decimalPlaces
        numberFormat.maximumFractionDigits = decimalPlaces
        
        // Disable grouping separators for cleaner Forex price display (e.g., 1.08452 instead of 1.084,52 depending on locale)
        numberFormat.isGroupingUsed = false

        return numberFormat
    }

    private fun toJavaCurrency(currency: Currency): JavaCurrency {
        return try {
            JavaCurrency.getInstance(currency.name)
        } catch (e: IllegalArgumentException) {
            JavaCurrency.getInstance(Currency.USD.name)
        }
    }

    private fun withNumericSpan(
        format: DecimalFormat,
        value: BigDecimal,
        transform: (formatted: String, start: Int, end: Int) -> String,
    ): String? {
        val iter = format.formatToCharacterIterator(value)
        val formatted = StringBuilder()
        var spanStart = -1
        var spanEnd = -1

        var char = iter.first()
        while (char != CharacterIterator.DONE) {
            val isNumeric = iter.attributes.keys.any { it in numericFields }
            if (isNumeric) {
                if (spanStart == -1) spanStart = formatted.length
                spanEnd = formatted.length + 1
            }
            formatted.append(char)
            char = iter.next()
        }

        if (spanStart == -1 || spanEnd == -1) return null
        return transform(formatted.toString(), spanStart, spanEnd)
    }

    private fun defaultMissingFallback(currency: Currency, formatLocale: Locale): String {
        return toJavaCurrency(currency).getSymbol(formatLocale) + MISSING_VALUE_PLACEHOLDER
    }

    private val numericFields = setOf<AttributedCharacterIterator.Attribute>(
        NumberFormat.Field.INTEGER,
        NumberFormat.Field.FRACTION,
        NumberFormat.Field.DECIMAL_SEPARATOR,
    )
}
