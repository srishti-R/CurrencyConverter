package com.sample.currencyconverter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.currencyconverter.data.CurrencyListItem
import com.sample.currencyconverter.db.CurrencyDbRepo
import com.sample.currencyconverter.network.CurrencyNetworkRepository

class CurrenciesViewModel(private val repository: CurrencyNetworkRepository, private val currencyDbRepo: CurrencyDbRepo) : ViewModel() {
    private val _listOfCurrencyCode = MutableLiveData<List<String>>()
    val listOfCurrencyCode: LiveData<List<String>> = _listOfCurrencyCode

    val showError = repository.showLiveError

    fun getAllCurrencyCodes() {
        _listOfCurrencyCode.postValue(repository.getAllCurrencyCodes())
    }

    suspend fun getAllCurrencyExchangeRates() {
        repository.getAllCurrencyExchangeRates()
    }

    fun getAllCurrencies(): LiveData<List<CurrencyListItem>> {
        return currencyDbRepo.getAllCurrencies()
    }
}