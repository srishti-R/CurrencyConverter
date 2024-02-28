package com.sample.currencyconverter.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sample.currencyconverter.data.CurrencyListItem
import com.sample.currencyconverter.utils.DB_NAME

@Database(entities = [CurrencyListItem::class], version = 1)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
    companion object {
        private var INSTANCE: CurrencyDatabase? = null
        fun getInstance(context: Context): CurrencyDatabase {
            if (INSTANCE == null) {
                synchronized(CurrencyDatabase::class) {
                    INSTANCE = buildRoomDB(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildRoomDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CurrencyDatabase::class.java,
                DB_NAME
            ).build()
    }
}