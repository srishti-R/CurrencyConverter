package com.sample.currencyconverter.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sample.currencyconverter.R
import com.sample.currencyconverter.data.CurrencyListItem
import com.sample.currencyconverter.utils.calculateCurrencyValue
import com.sample.currencyconverter.utils.roundToString

class CurrencyConvertingAdapter(
    private val currencyRateList: MutableList<CurrencyListItem>,
    private var baseCurrencyCode: String,
    private var baseCurrencyRateInUsd: Float,
    private var baseCurrencyValue: Float
) : RecyclerView.Adapter<CurrencyConvertingAdapter.CurrencyHolder>() {

    class CurrencyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currCode: TextView = itemView.findViewById(R.id.currency_code)
        val currName: TextView = itemView.findViewById(R.id.currency_name)
        val flag: TextView = itemView.findViewById(R.id.country_flag)
        val convertedValue: TextView = itemView.findViewById(R.id.currency_converted_value)
        val exchangeRate: TextView = itemView.findViewById(R.id.exchange_rate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_item, parent, false)
        return CurrencyHolder(view)
    }

    override fun getItemCount(): Int {
        return currencyRateList.size
    }

    override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
        val currencyListItem = currencyRateList[position]
        holder.flag.text = currencyListItem.flag
        holder.currCode.text = currencyListItem.currencyCode
        holder.currName.text = currencyListItem.currencyName
        val rate = calculateCurrencyValue(currencyListItem.rateInUSD, baseCurrencyRateInUsd)
        holder.exchangeRate.text =
            "1 $baseCurrencyCode = ${rate.roundToString()} ${currencyListItem.currencyCode}"
        holder.convertedValue.text = "${(baseCurrencyValue * rate).roundToString()}"
    }

    fun refreshData(list: List<CurrencyListItem>, baseCode: String, baseCurrRate: Float, baseCurrValue: Float) {
        if(currencyRateList != list) {
            currencyRateList.clear()
            currencyRateList.addAll(list)
        }

        baseCurrencyCode = baseCode
        baseCurrencyRateInUsd = baseCurrRate
        baseCurrencyValue = baseCurrValue
        notifyDataSetChanged()
    }
}


