package com.satryaway.paypaychallenge

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.satryaway.paypaychallenge.repos.LiveRepository
import com.satryaway.paypaychallenge.utils.Cache
import com.satryaway.paypaychallenge.utils.ListAdapter
import com.satryaway.paypaychallenge.utils.StringUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var liveRepository = LiveRepository()
    private var listAdapter = ListAdapter()
    private var quotes = hashMapOf<String, Float>()
    private var currencyList = arrayListOf<String>()
    private val DEFAULT_CURRENCY = "USD"
    private val DEFAULT_NOMINAL = 1f
    private var currentCurrency = DEFAULT_CURRENCY
    private var currentRate = 1f
    private var currentNominal = DEFAULT_NOMINAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestCurrencies()
        initAdapter()
        et_input_nominal.setText("1")

        btn_convert.setOnClickListener {
            requestCurrencies()
            convertRate()
        }
    }

    @VisibleForTesting
    fun convertRate() {
        currentNominal = et_input_nominal.text.toString().toFloat()
        if (currentNominal <= 0) {
            Toast.makeText(this,
                "Please Input Correct Value", Toast.LENGTH_SHORT).show()
        } else {
            setValueToAdapter()
        }
    }

    @VisibleForTesting
    fun requestCurrencies() {
        val cache = Cache.get(this)
        cache?.apply {
            if (isCurrenciesExisted()) {
                quotes = getCurrencies()
                Log.d("result", quotes.toString())
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    val result = liveRepository.live()
                    result.quotes?.let {
                        quotes = it
                        saveCurrencies(quotes)
                        Log.d("result", quotes.toString())
                    }
                }
            }
            currencyList = StringUtils.getCurrenciesValue(quotes)
        }
    }

    @VisibleForTesting
    fun initAdapter() {
        recycler_view.adapter = listAdapter
        recycler_view.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            StringUtils.getCurrenciesValue(quotes))
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(currency_spinner) {
            adapter = arrayAdapter
            setSelection(0, false)
            gravity = Gravity.CENTER
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    currentCurrency = currencyList[position]
                }
            }
        }
    }

    fun getRateFromCurrencyList(currency: String): Float {
        return quotes[currency] ?: 1f
    }

    @VisibleForTesting
    fun setValueToAdapter() {
        listAdapter.currency = currentCurrency
        listAdapter.nominal = currentNominal
        listAdapter.rate = getRateFromCurrencyList(currentCurrency)
        listAdapter.refresh(StringUtils.getCollectedList(quotes), quotes)
    }
}