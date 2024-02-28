package com.sample.currencyconverter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sample.currencyconverter.db.CurrencyDbRepo
import com.sample.currencyconverter.network.CurrencyNetworkRepository

class CurrenciesViewModelFactory(private val currencyNetworkRepository: CurrencyNetworkRepository, private val currencyDbRepo: CurrencyDbRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CurrenciesViewModel::class.java)) {
            CurrenciesViewModel(currencyNetworkRepository, currencyDbRepo) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}