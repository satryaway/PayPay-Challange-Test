package com.satryaway.paypaychallenge.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.satryaway.paypaychallenge.R
import com.satryaway.paypaychallenge.presenters.ConvertPresenter

class CurrentyRateListAdapter(private var presenter: ConvertPresenter) :
    RecyclerView.Adapter<CurrentyRateListAdapter.ViewHolder>() {
    var dataSet = arrayListOf<String>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val currencyText: TextView = view.findViewById(R.id.currency_text)
        val rateText: TextView = view.findViewById(R.id.rate_text)
    }

    fun refresh() {
        this.dataSet = presenter.getCollectedList()
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
        val currencyValue = StringUtils.getCurrencyValue(dataSet[position], true)
        val rate = presenter.currencyMap[currencyValue]
        val sourceRate = presenter.getSourceRate()
        val conversionRate = ((rate ?: 1f) / sourceRate) * presenter.currentNominal

        holder.currencyText.text = currencyValue
        holder.rateText.text = conversionRate.toString()
    }
}