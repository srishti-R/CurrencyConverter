package com.sample.currencyconverter.db

import android.content.Context
import android.content.SharedPreferences
import com.sample.currencyconverter.utils.LAST_UPDATED_TIMESTAMP
import com.sample.currencyconverter.utils.SHARED_PREFS_NAME

class SharedPreferencesManager(context: Context) {
    private val sharedPref: SharedPreferences? = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun getLastUpdatedTimestamp(): Long? {
        if(sharedPref != null) {
            return sharedPref.getLong(LAST_UPDATED_TIMESTAMP, 0L)
        }
        return null
    }

    fun setLastUpdatedTimestamp(timestamp: Long) {
        sharedPref?.edit()?.putLong(LAST_UPDATED_TIMESTAMP, timestamp)?.commit()
    }
}