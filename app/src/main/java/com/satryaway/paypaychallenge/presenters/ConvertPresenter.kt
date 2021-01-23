package com.satryaway.paypaychallenge.presenters

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.satryaway.paypaychallenge.repos.LiveRepository
import com.satryaway.paypaychallenge.utils.CacheUtils
import com.satryaway.paypaychallenge.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class ConvertPresenter {
    private var view: View? = null

    @VisibleForTesting
    val liveRepository = LiveRepository()

    var currencyList = arrayListOf<String>()
    var currentCurrency = "USD"
    var currentNominal = 1.0

    var currencyMap = hashMapOf<String, Double>()

    fun attachView(view: View) {
        this.view = view
    }

    fun detachView() {
        this.view = null
    }

    fun requestRate(context: Context) {
        val cache = CacheUtils.get(context)
        if (cache?.isCurrencyExpired() == true) {
            GlobalScope.launch(Dispatchers.IO) {
                val result = liveRepository.live()
                result.quotes?.let {
                    cache.saveCurrencies(it) { currencyMap, isSaved ->
                        if (isSaved) {
                            this@ConvertPresenter.currencyMap = currencyMap
                            view?.onFetchedCurrency(StringUtils.getCurrenciesValue(currencyMap))
                        } else {
                            view?.onFailedSavingCurrency("Failed to Store Data")
                        }
                    }
                }
            }
        } else {
            cache?.initCurrencies {
                this@ConvertPresenter.currencyMap = it
                view?.onFetchedCurrency(StringUtils.getCurrenciesValue(it))
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
            val text = "${StringUtils.getCurrencyInitial(it.key)};${it.value}"
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