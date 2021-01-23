package com.satryaway.paypaychallenge.presenters

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.satryaway.paypaychallenge.repos.ApiRepository
import com.satryaway.paypaychallenge.utils.CacheUtils
import com.satryaway.paypaychallenge.utils.StringUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

class ConvertPresenter {
    private var view: View? = null

    @VisibleForTesting
    val liveRepository = ApiRepository()

    var currencyList = arrayListOf<String>()
    var currentCurrency = "USD"
    var currentNominal = 1.0

    var currencyMap = TreeMap<String, Double>()
    var currencyNameMap = HashMap<String, String>()

    fun attachView(view: View) {
        this.view = view
    }

    fun detachView() {
        this.view = null
    }

    fun requestRate(context: Context) {
        val cache = CacheUtils.get(context)
        if (cache?.isCurrencyExpired() == true) {
            GlobalScope.launch {
                val result = liveRepository.live()
                val listCurrency = liveRepository.list()
                cache.saveCurrencies(
                    result.quotes,
                    listCurrency.currencies
                ) { currencyMap, currencyNameMap, isSaved ->
                    if (isSaved) {
                        this@ConvertPresenter.currencyNameMap = currencyNameMap
                        this@ConvertPresenter.currencyMap = currencyMap
                        view?.onFetchedCurrency(StringUtils.getCurrenciesValue(currencyMap))
                    } else {
                        view?.onFailedSavingCurrency("Failed to Store Data")
                    }
                }

            }
        } else {
            cache?.initCurrencies { currencyMap, currencyNameMap ->
                this@ConvertPresenter.currencyMap = currencyMap
                this@ConvertPresenter.currencyNameMap = currencyNameMap
                view?.onFetchedCurrency(StringUtils.getCurrenciesValue(currencyMap))
            }
        }
    }

    fun convert(nominal: String) {
        try {
            this.currentNominal = nominal.toDouble()
            if (currentNominal <= 0) {
                view?.showErrorMessage("Please Input Correct Value")
            } else {
                view?.setConversionValue()
            }
        } catch (exception: NumberFormatException) {
            view?.showErrorMessage("Please Input Numeric Value")
        }
    }

    fun getCollectedList(): ArrayList<String> {
        val list = arrayListOf<String>()
        currencyMap.forEach {
            val text = "${it.key};${it.value}"
            list.add(text)
        }

        return list
    }

    fun getSourceRate(): Double {
        return currencyMap[currentCurrency] ?: 1.0
    }

    interface View {
        fun setConversionValue()
        fun showErrorMessage(message: String)
        fun onFetchedCurrency(currenciesValue: ArrayList<String>)
        fun onFailedSavingCurrency(message: String)
    }
}