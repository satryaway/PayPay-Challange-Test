package com.satryaway.paypaychallenge

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.satryaway.paypaychallenge.presenters.ConvertPresenter
import com.satryaway.paypaychallenge.utils.DialogUtils
import com.satryaway.paypaychallenge.utils.ListAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    ConvertPresenter.View {
    private var listAdapter = ListAdapter()
    private var arrayAdapter: ArrayAdapter<String>? = null

    private val presenter: ConvertPresenter = ConvertPresenter()

    private var currencyList = arrayListOf<String>()
    private var currentCurrency = "USD"
    private var currentNominal = 1f

    private var requireInit = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.attachView(this)

        initAdapter()
        presenter.requestRate(this)

        btn_convert.setOnClickListener {
            currentNominal = et_input_nominal.text.toString().toFloat()
            presenter.convert(currentNominal)
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    private fun initAdapter() {
        // Init List Adapter
        recycler_view.adapter = listAdapter
        recycler_view.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )

        // Init Spinner Adapter
        arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            currencyList
        )
        arrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(currency_spinner) {
            adapter = arrayAdapter
            gravity = Gravity.CENTER
            onItemSelectedListener = this@MainActivity
        }
    }

    private fun refreshView() {
        // Reset Spinner Adapter
        arrayAdapter?.clear()
        arrayAdapter?.addAll(currencyList)
        arrayAdapter?.notifyDataSetChanged()

        // Reset List of Currency
        listAdapter.currency = currentCurrency
        listAdapter.nominal = currentNominal
        listAdapter.rate = presenter.currencyMap[currentCurrency] ?: 1f
        listAdapter.refresh(presenter.getCollectedList(), presenter.currencyMap)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentCurrency = currencyList[position]
        if (requireInit) {
            refreshView()
            requireInit = false
        }
    }

    override fun setConversionValue() {
        presenter.requestRate(this)
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onFetchedCurrency(currenciesValue: ArrayList<String>) {
        runOnUiThread {
            this.currencyList = currenciesValue
            refreshView()
        }
    }

    override fun onFailedSavingCurrency(message: String) {
        DialogUtils.showToast(this, message)
    }
}