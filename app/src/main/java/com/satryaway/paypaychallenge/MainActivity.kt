package com.satryaway.paypaychallenge

import android.os.Bundle
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
import com.satryaway.paypaychallenge.utils.DialogUtils
import com.satryaway.paypaychallenge.utils.ListAdapter
import com.satryaway.paypaychallenge.utils.StringUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var liveRepository = LiveRepository()
    private var listAdapter = ListAdapter()
    private var arrayAdapter: ArrayAdapter<String>? = null

    private var currencyList = arrayListOf<String>()
    private var currentCurrency = "USD"
    private var currentNominal = 1f

    private var cache: Cache? = null
    private var requireInit = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cache = Cache.get(this)

        initAdapter()
        requestCurrencies()
        initValues()

        btn_convert.setOnClickListener {
            requestCurrencies()
            convertRate()
        }
    }

    private fun initValues() {
        et_input_nominal.setText(currentNominal.toString())
        setValueToAdapter()
    }

    @VisibleForTesting
    fun convertRate() {
        currentNominal = et_input_nominal.text.toString().toFloat()
        if (currentNominal <= 0) {
            Toast.makeText(
                this,
                "Please Input Correct Value", Toast.LENGTH_SHORT
            ).show()
        } else {
            setValueToAdapter()
        }
    }

    @VisibleForTesting
    fun requestCurrencies() {
        cache?.apply {
            if (isCurrencyExpired()) {
                GlobalScope.launch(Dispatchers.IO) {
                    val result = liveRepository.live()
                    result.quotes?.let {
                        saveCurrencies(it) { isSaved ->
                            if (isSaved) {
                                runOnUiThread {
                                    currencyList = StringUtils
                                        .getCurrenciesValue(currencyMap)
                                    setValueToAdapter()
                                }
                            } else {
                                DialogUtils.showToast(baseContext, "Failed to Save Data")
                            }
                        }
                    }
                }
            } else {
                initCurrencies()
                currencyList = StringUtils.getCurrenciesValue(currencyMap)
            }
        }
    }

    @VisibleForTesting
    fun initAdapter() {
        // Init List Adapter
        recycler_view.adapter = listAdapter
        recycler_view.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )

        // Init Spinner Adapter
        arrayAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            currencyList
        )
        arrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(currency_spinner) {
            adapter = arrayAdapter
            gravity = Gravity.CENTER
            onItemSelectedListener = this@MainActivity
        }
    }

    @VisibleForTesting
    fun getCollectedList(): ArrayList<String> {
        val list = arrayListOf<String>()
        cache?.currencyMap?.forEach {
            val text = "${StringUtils.getCurrencyInitial(it.key)};${it.value}"
            list.add(text)
        }

        return list
    }

    @VisibleForTesting
    fun setValueToAdapter() {
        arrayAdapter?.clear()
        arrayAdapter?.addAll(currencyList)
        arrayAdapter?.notifyDataSetChanged()
        listAdapter.currency = currentCurrency
        listAdapter.nominal = currentNominal

        cache?.currencyMap?.let {
            listAdapter.rate = it[currentCurrency] ?: 1f
            listAdapter.refresh(getCollectedList(), it)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentCurrency = currencyList[position]
        if(requireInit) {
            setValueToAdapter()
            requireInit = false
        }
    }
}