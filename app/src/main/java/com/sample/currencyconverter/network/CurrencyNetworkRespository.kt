package com.sample.currencyconverter.network

import androidx.lifecycle.MutableLiveData
import com.sample.currencyconverter.data.CurrencyListItem
import com.sample.currencyconverter.data.CurrencyRateItem
import com.sample.currencyconverter.db.CurrencyDbRepo
import com.sample.currencyconverter.utils.supportedCurrencyList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrencyNetworkRepository(
    private val currencyRetrofitService: CurrencyRetrofitService,
    private val currencyDbService: CurrencyDbRepo
) {

    val showLiveError = MutableLiveData<Boolean>()
    suspend fun getAllCurrencyExchangeRates() {
        val list = mutableListOf<CurrencyListItem>()
        val call = currencyRetrofitService.getExchangeRatesForBaseUSD()
        call.enqueue(object : Callback<CurrencyRateItem?> {
            override fun onResponse(
                call: Call<CurrencyRateItem?>,
                response: Response<CurrencyRateItem?>
            ) {
                val data = response.body()
                val rates = data?.rates
                rates?.forEach { (key, value) ->
                    val array = supportedCurrencyList[key]
                    val listItem = CurrencyListItem(
                        key,
                        array?.get(0) ?: "",
                        value,
                        array?.get(1) ?: ""
                    )
                    list.add(listItem)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    run(this) {
                        withContext(Dispatchers.IO) {
                            if (currencyDbService.isDbEmpty()) {
                                currencyDbService.insertAllCurrencies(list)
                            } else {
                                currencyDbService.updateCurrencies(list)
                            }
                        }
                    }
                }
                setError(false)
            }

            override fun onFailure(call: Call<CurrencyRateItem?>, t: Throwable) {
                setError(true)
            }
        })

    }

    private suspend fun run(
        coroutineScope: CoroutineScope,
        lambda: suspend CoroutineScope.() -> Unit
    ) {
        lambda.invoke(coroutineScope)
    }

    fun getAllCurrencyCodes(): List<String> {
        val list = mutableListOf<String>()
        val call = currencyRetrofitService.getAllCurrencies()
        call.enqueue(object : Callback<Map<String, String>?> {
            override fun onResponse(
                call: Call<Map<String, String>?>,
                response: Response<Map<String, String>?>
            ) {
                val currencies: Map<String, String>? = response.body()
                currencies?.forEach { (currCode, _) ->
                    list.add(currCode)
                }
                setError(false)
            }

            override fun onFailure(call: Call<Map<String, String>?>, t: Throwable) {
                setError(true)
            }
        })
        return list
    }

    fun setError(showError: Boolean) {
        showLiveError.postValue(showError)
    }
}