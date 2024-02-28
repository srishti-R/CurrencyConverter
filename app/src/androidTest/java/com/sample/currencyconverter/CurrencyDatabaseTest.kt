package com.sample.currencyconverter

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.sample.currencyconverter.data.CurrencyListItem
import com.sample.currencyconverter.db.CurrencyDao
import com.sample.currencyconverter.db.CurrencyDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CurrencyDatabaseTest {
    private lateinit var database: CurrencyDatabase
    private lateinit var currencyDao: CurrencyDao

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CurrencyDatabase::class.java
        ).allowMainThreadQueries().build()

        currencyDao = database.currencyDao()
    }

    @Test
    fun insertList_returnsSameList() = runBlocking {
        currencyDao.insertAllCurrencies(LIST_OF_EXCHANGE_RATES)
        val list = currencyDao.getAllCurrencies().getOrAwaitValue()
        assertTrue(
            list.size == LIST_OF_EXCHANGE_RATES.size && list.containsAll(
                LIST_OF_DB_STORED_RATES
            ) && LIST_OF_DB_STORED_RATES.containsAll(list)
        )
    }

    @Test
    fun insertEmptyList_returnsEmptyList() = runBlocking {
        currencyDao.insertAllCurrencies(emptyList())
        val list = currencyDao.getAllCurrencies().getOrAwaitValue()
        assertTrue(list.isEmpty())
    }

    @Test
    fun insertList_returnsListOfCurrencyCodes() = runBlocking {
        currencyDao.insertAllCurrencies(LIST_OF_EXCHANGE_RATES)
        val list = currencyDao.getAllCurrencyCodes()
        assertTrue(list.isNotEmpty() && list.containsAll(LIST_OF_CURRENCY_CODES))
    }

    @Test
    fun insertList_updateCurrency_returnsUpdatedListOfCurrency() = runBlocking {
        val currency = CurrencyListItem(1, "AWG", "any", 4F, "🇯🇵")
        currencyDao.insertAllCurrencies(listOf(currency))
        val updated = CurrencyListItem(1, "AED", "any", 4F, "🇯🇵")
        val list = listOf(updated)
        currencyDao.updateCurrencies(list)
        val updatedList = currencyDao.getAllCurrencies().getOrAwaitValue()
        assertTrue(updatedList.isNotEmpty() && updatedList[0].currencyCode == "AED")
    }

    @Test
    fun insertEmptyList_returnsDbEmpty() = runBlocking {
        currencyDao.insertAllCurrencies(emptyList())
        assertTrue(currencyDao.isEmpty())
    }

    @After
    fun closeDatabase() {
        database.close()
    }
}