package com.satryaway.paypaychallenge.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.satryaway.paypaychallenge.R
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ListAdapter() : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    var dataSet = arrayListOf<String>()
    var currency = ""
    var nominal = 1f
    var mapsOfCurrency = hashMapOf<String, Float>()
    var rate = 1f


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val currencyText: TextView = view.findViewById(R.id.currency_text)
        val rateText: TextView = view.findViewById(R.id.rate_text)
    }

    fun refresh(
        dataSet: ArrayList<String>,
        mapsOfCurrency: HashMap<String, Float>
    ) {
        this.dataSet.clear()
        this.mapsOfCurrency.clear()
        this.dataSet = dataSet
        this.mapsOfCurrency = mapsOfCurrency
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_row_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.currencyText.text = StringUtils.getCurrencyValue(dataSet.get(position), true)
        val rate = mapsOfCurrency[StringUtils.getCurrencyValue(dataSet.get(position), true)]
        val conversionRate = ((rate ?: 1f) / this.rate) * nominal
        holder.rateText.text = conversionRate.toString()
    }
}