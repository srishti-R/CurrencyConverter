package com.sample.currencyconverter.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sample.currencyconverter.R
import com.sample.currencyconverter.data.CurrencyListItem
import com.sample.currencyconverter.db.CurrencyDatabase
import com.sample.currencyconverter.db.CurrencyDbRepo
import com.sample.currencyconverter.db.SharedPreferencesManager
import com.sample.currencyconverter.network.CurrencyNetworkRepository
import com.sample.currencyconverter.network.CurrencyRetrofitService
import com.sample.currencyconverter.utils.DEFAULT_CURRENCY_CODE
import com.sample.currencyconverter.utils.getLastUpdatedTimeFromTimestamp
import com.sample.currencyconverter.utils.shouldUpdateExchangeRates
import com.sample.currencyconverter.viewmodel.CurrenciesViewModel
import com.sample.currencyconverter.viewmodel.CurrenciesViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var currencyType: TextInputLayout
    private lateinit var currencyTypeSelector: AutoCompleteTextView
    private lateinit var lastUpdatedText: TextView
    private lateinit var currencyField: TextInputEditText
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPrefManager: SharedPreferencesManager
    private lateinit var currencyDbRepo: CurrencyDbRepo
    private lateinit var currencyNetworkRepository: CurrencyNetworkRepository
    private lateinit var viewModel: CurrenciesViewModel
    private lateinit var db: CurrencyDatabase
    private lateinit var exchangeRatesAdapter: CurrencyConvertingAdapter
    private val currencyRetrofitService = CurrencyRetrofitService.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        currencyType = findViewById(R.id.currency_type)
        currencyTypeSelector = findViewById(R.id.currency_type_value)
        currencyField = findViewById(R.id.currency_field)
        progressBar = findViewById(R.id.progress_bar)
        lastUpdatedText = findViewById(R.id.last_updated_timestamp)
        sharedPrefManager = SharedPreferencesManager(this)
        val lastUpdatedTimestamp = if (sharedPrefManager.getLastUpdatedTimestamp() != 0L) {
            sharedPrefManager.getLastUpdatedTimestamp()
        } else {
            lastUpdatedText.visibility = View.GONE
            null
        }
        lastUpdatedText.text = getString(R.string.last_updated) + " ${
            lastUpdatedTimestamp?.let {
                getLastUpdatedTimeFromTimestamp(
                    it
                )
            }
        }"
        db = CurrencyDatabase.getInstance(this)
        currencyDbRepo = CurrencyDbRepo(db)
        currencyNetworkRepository =
            CurrencyNetworkRepository(CurrencyRetrofitService.getInstance(), currencyDbRepo)
        viewModel = ViewModelProvider(
            this,
            CurrenciesViewModelFactory(
                CurrencyNetworkRepository(
                    currencyRetrofitService,
                    currencyDbRepo
                ), currencyDbRepo
            )
        )[CurrenciesViewModel::class.java]
        currencyTypeSelector.inputType = InputType.TYPE_NULL
        currencyTypeSelector.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val inputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.applicationWindowToken, 0)
            }
        }
        currencyField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (currencyTypeSelector.text.isNotEmpty() && currencyField.text?.isNotEmpty() == true) {
                    getExchangeRates(currencyTypeSelector.text.toString())
                }
            }
        })
        currencyField.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_DONE || p1 == EditorInfo.IME_ACTION_NEXT) {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(p0?.windowToken, 0)
                    if (currencyTypeSelector.text.isNotEmpty() && currencyField.text?.isNotEmpty() == true) {
                        getExchangeRates(currencyTypeSelector.text.toString())
                    }
                    return true
                }
                return false
            }
        })
        getAllCurrencies()
    }

    override fun onResume() {
        super.onResume()
        exchangeRatesAdapter = CurrencyConvertingAdapter(mutableListOf(), DEFAULT_CURRENCY_CODE, 0F, 0F)
        val recyclerview = findViewById<RecyclerView>(R.id.currency_recycler)
        recyclerview.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerview.adapter = exchangeRatesAdapter
        getAllCurrencies()
        if (currencyTypeSelector.text.isNotEmpty() && currencyField.text?.isNotEmpty() == true) {
            getExchangeRates(currencyTypeSelector.text.toString())
        }
    }

    private fun getAllCurrencies() {
        progressBar.visibility = View.VISIBLE
        val list = mutableListOf<String>()
        if (shouldUpdateExchangeRates(sharedPrefManager.getLastUpdatedTimestamp() ?: 0L)) {
            viewModel.showError.observe(this) {
                if (it) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.api_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            viewModel.listOfCurrencyCode.observe(this) {
                setUpAllCurrenciesAdapter(it)
            }
            viewModel.getAllCurrencyCodes()
        } else {
            lifecycleScope.launch {
                list.addAll(currencyDbRepo.getAllCurrencyCodes())
                setUpAllCurrenciesAdapter(list)
            }
        }
    }

    private fun getExchangeRates(base: String? = DEFAULT_CURRENCY_CODE) {
        val list = mutableListOf<CurrencyListItem>()
        progressBar.visibility = View.VISIBLE
        if (shouldUpdateExchangeRates(sharedPrefManager.getLastUpdatedTimestamp() ?: 0L)) {
            sharedPrefManager.setLastUpdatedTimestamp(System.currentTimeMillis())
            lastUpdatedText.visibility = View.VISIBLE
            lastUpdatedText.text = getString(R.string.last_updated) + " ${
                getLastUpdatedTimeFromTimestamp(sharedPrefManager.getLastUpdatedTimestamp() ?: System.currentTimeMillis())
            }"
            viewModel.showError.observe(this) {
                if (it) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.api_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            viewModel.getAllCurrencies().observe(this@MainActivity) { currencyList ->
                list.addAll(currencyList)
                val baseCurrencyValue = if (list.isNotEmpty()) {
                    list.filter { it.currencyCode == base }[0].rateInUSD
                } else {
                    0F
                }
                if (list.isNotEmpty()) {
                    setUpExchangeRatesAdapter(list, base, baseCurrencyValue)
                }
            }
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.getAllCurrencyExchangeRates()
            }
        } else {
            viewModel.getAllCurrencies().observe(this@MainActivity) { currencyList ->
                list.addAll(currencyList)
                val baseCurrencyValue = if (list.isNotEmpty()) {
                    list.filter { it.currencyCode == base }[0].rateInUSD
                } else {
                    0F
                }
                if (list.isNotEmpty()) {
                    setUpExchangeRatesAdapter(list, base, baseCurrencyValue)
                }
            }
        }
    }

    private fun setUpAllCurrenciesAdapter(list: List<String>) {
        val adapter = ArrayAdapter(this@MainActivity, R.layout.list_item, list)
        if(currencyTypeSelector.text.isEmpty()) currencyTypeSelector.setText(DEFAULT_CURRENCY_CODE, false)
        currencyTypeSelector.setAdapter(adapter)
        currencyTypeSelector.onItemClickListener =
            AdapterView.OnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
                if (currencyField.text?.isEmpty() == true) {
                    currencyField.error = getString(R.string.currency_field_error)
                    return@OnItemClickListener
                }
                getExchangeRates(list[i])
            }
        progressBar.visibility = View.GONE
    }

    private fun setUpExchangeRatesAdapter(
        list: List<CurrencyListItem>,
        base: String? = DEFAULT_CURRENCY_CODE,
        baseCurrencyValue: Float
    ) {
        val currencyValue =
            if (currencyField.text?.toString()?.isNotEmpty() == true) {
                currencyField.text?.toString()?.toFloat()
            } else {
                0F
            }
        exchangeRatesAdapter.refreshData(list, base ?: DEFAULT_CURRENCY_CODE, baseCurrencyValue, currencyValue ?:  0F)
        progressBar.visibility = View.GONE
    }
}