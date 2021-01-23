package com.satryaway.paypaychallenge.utils

import java.util.*
import kotlin.collections.ArrayList

object StringUtils {
    fun getCurrencyValue(text: String, isKey: Boolean): String {
        return try {
            val key = text.split(";")
            if (isKey)
                key[0]
            else
                key[1]
        } catch (e: Exception) {
            text
        }
    }

    fun getCurrenciesValue(quotes: TreeMap<String, Double>): ArrayList<String> {
        val list = arrayListOf<String>()
        quotes.forEach {
            list.add(it.key)
        }

        return list
    }

    fun getCurrencyInitial(text: String): String {
        return if (text.count() >= 3) {
            text.takeLast(3)
        } else {
            text
        }
    }

    fun getThousandSeparator(conversionRate: Double): String {
        return String.format("%,.3f", conversionRate)
    }
}