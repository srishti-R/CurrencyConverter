package com.sample.currencyconverter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sample.currencyconverter.utils.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class CurrencyListItem(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo var currencyCode: String,
    @ColumnInfo val currencyName: String,
    @ColumnInfo val rateInUSD: Float,
    @ColumnInfo val flag: String
) {
    constructor(
        currencyCode: String,
        currencyName: String,
        rateInUSD: Float,
        flag: String
    ) : this(0, currencyCode, currencyName, rateInUSD, flag)
}
