package com.sample.currencyconverter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.sample.currencyconverter.data.CurrencyListItem
import com.sample.currencyconverter.db.CurrencyDbRepo
import com.sample.currencyconverter.network.CurrencyNetworkRepository
import com.sample.currencyconverter.viewmodel.CurrenciesViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class CurrencyViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun currencyViewModel_getCurrencyCodes_returnsLivedataOfList() {
        val currencyService = mock(CurrencyNetworkRepository::class.java)
        val currencyDbRepo = mock(CurrencyDbRepo::class.java)
        val currencyViewModel = CurrenciesViewModel(currencyService, currencyDbRepo)
        Mockito.`when`(currencyService.getAllCurrencyCodes()).thenReturn(LIST_OF_CURRENCY_CODES)
        currencyViewModel.getAllCurrencyCodes()
        Assert.assertEquals(currencyViewModel.listOfCurrencyCode.getOrAwaitValue(), LIST_OF_CURRENCY_CODES)
    }

    @Test
    fun currencyViewModel_getExchangeRates_returnsLivedataOfRates() = runTest {
        val currencyService = mock(CurrencyNetworkRepository::class.java)
        val currencyDbRepo = mock(CurrencyDbRepo::class.java)
        val dataResponse = MutableLiveData<List<CurrencyListItem>>()
        dataResponse.value = LIST_OF_DB_STORED_RATES
        val currencyViewModel = CurrenciesViewModel(currencyService, currencyDbRepo)
        Mockito.`when`(currencyDbRepo.getAllCurrencies()).thenReturn(
                dataResponse)
        Assert.assertEquals(currencyViewModel.getAllCurrencies().getOrAwaitValue(), LIST_OF_DB_STORED_RATES)
    }
}