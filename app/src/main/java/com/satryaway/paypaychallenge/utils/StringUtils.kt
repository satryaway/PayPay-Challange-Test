package com.satryaway.paypaychallenge.utils

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

    fun getCurrenciesValue(quotes: HashMap<String, Float>): ArrayList<String> {
        val list = arrayListOf<String>()
        quotes.forEach {
            list.add(getCurrencyInitial(it.key))
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
}