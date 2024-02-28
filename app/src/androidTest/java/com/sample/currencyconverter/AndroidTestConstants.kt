package com.sample.currencyconverter

import com.sample.currencyconverter.data.CurrencyListItem

val LIST_OF_EXCHANGE_RATES = listOf(
    CurrencyListItem("AED", "Dirham", 2F, "🇦🇪"),
    CurrencyListItem("INR", "rupee", 80F, "🇮🇳"),
    CurrencyListItem("JPY", "japanese yen", 34F, "🇯🇵")
)

val LIST_OF_DB_STORED_RATES = listOf(
    CurrencyListItem(1, "AED", "Dirham", 2F, "🇦🇪"),
    CurrencyListItem(2, "INR", "rupee", 80F, "🇮🇳"),
    CurrencyListItem(3, "JPY", "japanese yen", 34F, "🇯🇵")
)
val LIST_OF_CURRENCY_CODES = listOf("AED", "INR", "JPY")

