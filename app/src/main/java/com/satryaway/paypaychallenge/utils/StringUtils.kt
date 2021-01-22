package com.satryaway.paypaychallenge.utils

import androidx.annotation.VisibleForTesting
import java.lang.Exception

object StringUtils {
    fun getCurrencyValue(text: String, isKey: Boolean): String {
        return try {
            val key = text.split(";")
            if (isKey)
                key[0]
            else
                key[1]
        } catch (e: Exception) {
            ""
        }
    }

    fun modifyCurrencyName(quotes: HashMap<String, Float>): HashMap<String, Float> {
        val mapsOfCurrency = hashMapOf<String, Float>()
        quotes.forEach {
            mapsOfCurrency[getCurrencyInitial(it.key)] = it.value
        }

        return mapsOfCurrency
    }

    fun getCurrenciesValue(quotes: HashMap<String, Float>): ArrayList<String> {
        val list = arrayListOf<String>()
        quotes.forEach {
            list.add(getCurrencyInitial(it.key))
        }

        return list
    }

    fun getRateFromSelectedCurrency(currency: String, currencyList: ArrayList<String>) {
        if (currencyList.contains(currency)) {

        }
    }

    fun getCurrencyInitial(text: String): String {
        return if (text.count() >= 3) {
            text.takeLast(3)
        } else {
            text
        }
    }
}