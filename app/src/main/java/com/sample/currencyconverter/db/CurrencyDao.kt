package com.sample.currencyconverter.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.sample.currencyconverter.data.CurrencyListItem
import com.sample.currencyconverter.utils.DB_NAME
import com.sample.currencyconverter.utils.TABLE_NAME

@Dao
interface CurrencyDao {

    @Query("SELECT currencyCode FROM $TABLE_NAME")
    suspend fun getAllCurrencyCodes(): List<String>

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAllCurrencies(): LiveData<List<CurrencyListItem>>

    @Insert
    suspend fun insertAllCurrencies(currencies: List<CurrencyListItem>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCurrencies(currencyList: List<CurrencyListItem>)

    @Query("SELECT (SELECT COUNT(*) FROM $TABLE_NAME) == 0")
    suspend fun isEmpty(): Boolean
}