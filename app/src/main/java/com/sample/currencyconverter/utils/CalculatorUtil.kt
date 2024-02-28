package com.sample.currencyconverter.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Calculates the value of a given currency wrt base currency
 * @param fromCurrencyExchangeRate the rate of base currency wrt USD
 * @param toCurrencyExchangeRate the rate of other currency in which the value needs to be converted wrt USD
 * @return the calculated value of 1 base currency = result times other currency eg: 1 INR = 1.78 JPY
 */
fun calculateCurrencyValue(fromCurrencyExchangeRate: Float, toCurrencyExchangeRate: Float): Float {
    return fromCurrencyExchangeRate / toCurrencyExchangeRate
}

/**
 * Attempt an api call if current time diff with the time the last api call was made is more than 30 min
 * else fetch data from local db
 * @param lastApiCallTimestamp the last recorded timestamp when api call was made, saved in shared prefs
 * @return whether to make an api call or skip
 */
fun shouldUpdateExchangeRates(lastApiCallTimestamp: Long): Boolean {
    val lastCallTime = Date(lastApiCallTimestamp).time
    val currentTime = Date(System.currentTimeMillis()).time
    return ((currentTime - lastCallTime) / (60 * 1000)) > 30
}

/**
 * returns a string with float number's end zeros trimmed if no significant digit is there in first
 * 4 digits after the decimal, trims the decimal also in case the ending zeros were all removed
 * eg: 2.0000004 => 2.0000 => 2. => 2
 * @return formatted string of end result
 */
fun Float.roundToString() = "%.4f".format(this).trimEnd('0').trimEnd('.')

/**
 * @param timestamp when last api call was made
 * @return formatted timestamp for the time when last api call was made
 */
fun getLastUpdatedTimeFromTimestamp(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a")
        val netDate = Date(timestamp)
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}
