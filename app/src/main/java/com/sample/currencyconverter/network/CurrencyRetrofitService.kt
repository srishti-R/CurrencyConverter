package com.sample.currencyconverter.network

import com.sample.currencyconverter.data.CurrencyRateItem
import com.sample.currencyconverter.utils.BASE_URL
import com.sample.currencyconverter.utils.GET_CURRENCY_LIST_URL
import com.sample.currencyconverter.utils.GET_EXCHANGE_RATES_URL
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CurrencyRetrofitService {
    @GET(GET_CURRENCY_LIST_URL)
    fun getAllCurrencies(): Call<Map<String, String>?>

    @GET(GET_EXCHANGE_RATES_URL)
    fun getExchangeRatesForBaseUSD(): Call<CurrencyRateItem?>

    companion object {
        private var retrofitService: CurrencyRetrofitService? = null

        fun getInstance() : CurrencyRetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(CurrencyRetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}