package com.satryaway.paypaychallenge.presenters

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.satryaway.paypaychallenge.BuildConfig
import com.satryaway.paypaychallenge.mocks.MockData
import com.satryaway.paypaychallenge.models.CurrencyModel
import com.satryaway.paypaychallenge.models.LiveModel
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
                var result: LiveModel? = null
                var listCurrency: CurrencyModel? = null
                if (BuildConfig.DEBUG) {
                    result = MockData.getLiveMock()
                    listCurrency = MockData.getCurrencyMock()
                } else {
                    result = liveRepository.live()
                    listCurrency = liveRepository.list()
                }
                handleRequestRate(cache, result, listCurrency)
            }
        } else {
            cache?.initCurrencies { currencyMap, currencyNameMap ->
                this@ConvertPresenter.currencyMap = currencyMap
                this@ConvertPresenter.currencyNameMap = currencyNameMap
                view?.onFetchedCurrency(StringUtils.getCurrenciesValue(currencyMap))
            }
        }
    }

    fun handleRequestRate(
        cache: CacheUtils?,
        result: LiveModel?,
        listCurrency: CurrencyModel?
    ) {
        if (result != null && listCurrency != null) {
            if (result.success == true && listCurrency.success == true) {
                cache?.saveCurrencies(
                    result.quotes,
                    listCurrency.currencies
                ) { currencyMap, currencyNameMap, isSaved ->
                    if (isSaved) {
                        this@ConvertPresenter.currencyNameMap = currencyNameMap
                        this@ConvertPresenter.currencyMap = currencyMap
                        view?.onFetchedCurrency(StringUtils.getCurrenciesValue(currencyMap))
                    } else {
                        view?.onFailedFetchingCurrency("Failed to Fetch Data")
                    }
                }
            } else {
                view?.onFailedFetchingCurrency(
                    when {
                        result.error != null -> {
                            result.error.info
                        }
                        listCurrency.error != null -> {
                            listCurrency.error.info
                        }
                        else -> {
                            "Unknown Error"
                        }
                    }
                )
            }
        } else {
            view?.onFailedFetchingCurrency("Unknown Error")
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
        fun onFailedFetchingCurrency(message: String)
    }
}