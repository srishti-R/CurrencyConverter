package com.sample.currencyconverter

import com.sample.currencyconverter.utils.calculateCurrencyValue
import com.sample.currencyconverter.utils.getLastUpdatedTimeFromTimestamp
import com.sample.currencyconverter.utils.roundToString
import com.sample.currencyconverter.utils.shouldUpdateExchangeRates
import org.junit.Assert
import org.junit.Test

class CalculatorUtilTest {

    @Test
    fun calculator_getValueOfBaseCurrencyJPYInINRCurrency_returnsCorrectExchangeRate() {
       val result = calculateCurrencyValue(0.0067F, 0.012F)
        Assert.assertEquals(result, 0.55833334F)
    }

    @Test
    fun calculator_getValueWhenNothingTypedInValueForBaseCurrency_takesValueAsZero_returnsZero() {
        val result = calculateCurrencyValue(0F, 0.012F)
        Assert.assertEquals(result, 0.0F)
    }

    @Test
    fun calculator_shouldUpdateExchangeRates_whenTimeDiffMoreThanThirtyMinutes_returnsTrue() {
        val lastFetchedTime = System.currentTimeMillis() - 35*60*1000
        Assert.assertEquals(shouldUpdateExchangeRates(lastFetchedTime), true)
    }

    @Test
    fun calculator_shouldUpdateExchangeRates_whenTimeDiffLessThanThirtyMinutes_returnsFalse() {
        val lastFetchedTime = System.currentTimeMillis() - 5*60*1000
        Assert.assertEquals(shouldUpdateExchangeRates(lastFetchedTime), false)
    }

    @Test
    fun calculator_roundToString_trimsEndZerosAndDecimal() {
        Assert.assertEquals((2.0000F).roundToString(), "2")
    }

    @Test
    fun calculator_roundToString_trimsEndZerosIfFoundInFirstFourDecimalPlaces() {
        Assert.assertEquals((2.00000007F).roundToString(), "2")
    }

    @Test
    fun calculator_roundToString_doesNotTrimsEndZerosIfSignificantDigitInFirstFourDecimalPlaces() {
        Assert.assertEquals((2.0004F).roundToString(), "2.0004")
    }

    @Test
    fun calculator_getLastUpdatedTimeFromTimestamp_returnsTimestampInCorrectFormat() {
        Assert.assertEquals(getLastUpdatedTimeFromTimestamp(1707651615000L), "11/02/2024 05:10 pm")
    }
}