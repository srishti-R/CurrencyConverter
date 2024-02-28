package com.sample.currencyconverter.db

import androidx.lifecycle.LiveData
import com.sample.currencyconverter.data.CurrencyListItem

class CurrencyDbRepo(private val currencyDb: CurrencyDatabase) {

     suspend fun getAllCurrencyCodes() : List<String>{
        return currencyDb.currencyDao().getAllCurrencyCodes()
    }
    fun getAllCurrencies() : LiveData<List<CurrencyListItem>> {
       return currencyDb.currencyDao().getAllCurrencies()
   }

    suspend fun insertAllCurrencies(currencyList: List<CurrencyListItem>) {
        currencyDb.currencyDao().insertAllCurrencies(currencyList)
    }

    suspend fun updateCurrencies(currencyList: List<CurrencyListItem>) {
        currencyDb.currencyDao().updateCurrencies(currencyList)
    }

    suspend fun isDbEmpty(): Boolean {
       return currencyDb.currencyDao().isEmpty()
    }
}