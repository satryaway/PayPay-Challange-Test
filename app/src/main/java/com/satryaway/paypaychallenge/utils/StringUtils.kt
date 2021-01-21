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

    @VisibleForTesting
    fun getCollectedList(quotes: HashMap<String, Float>): ArrayList<String> {
        val list = arrayListOf<String>()
        quotes.forEach {
            val text = "${StringUtils.getCurrencyInitial(it.key)};${it.value}"
            list.add(text)
        }

        return list
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

    private fun getCurrencyInitial(text: String): String {
        return if (text.count() >= 3) {
            text.takeLast(3)
        } else {
            text
        }
    }
}