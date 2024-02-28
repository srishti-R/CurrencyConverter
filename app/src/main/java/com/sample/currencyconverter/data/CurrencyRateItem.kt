package com.sample.currencyconverter.data

data class CurrencyRateItem(
    val disclaimer: String, val license: String, val timestamp: Long,
    val base: String, val rates: Map<String, Float>
)
