package com.satryaway.paypaychallenge.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.HashMap

class CacheUtils(pref: SharedPreferences) {

    private val preferences = pref

    fun isCurrencyExpired(): Boolean {
        val isCacheEmpty = preferences.getString(Constants.CURRENCY, "").isNullOrEmpty()
        val timeFlag = Date(preferences.getLong(Constants.TIME_FLAG, 0))

        return isMoreThanDesignatedTimeToFetchCurrency(timeFlag) || isCacheEmpty
    }

    fun isMoreThanDesignatedTimeToFetchCurrency(timeFlag: Date): Boolean {
        val timeNow: Long = Calendar.getInstance().time.time
        val flag = timeFlag.time
        val subs = timeNow - flag
        return subs >= Constants.RANGE_BETWEEN_LAST_CURRENCY_FETCH
    }

    fun saveCurrencies(
        quotes: HashMap<String, Double>?,
        onSave: (HashMap<String, Double>, Boolean) -> Unit
    ) {
        if (quotes != null) {
            val currencyMap = hashMapOf<String, Double>()
            quotes.forEach {
                val currency = StringUtils.getCurrencyInitial(it.key)
                currencyMap[currency] = it.value
            }
            val jsonString = Gson().toJson(currencyMap)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString(Constants.CURRENCY, jsonString)
            editor.apply()

            val timeNow = Calendar.getInstance().time
            preferences.edit().putLong(Constants.TIME_FLAG, timeNow.time).apply()

            onSave.invoke(currencyMap, true)
        } else {
            onSave.invoke(hashMapOf(), false)
        }
    }

    fun initCurrencies(onFetchCurrency: (HashMap<String, Double>) -> Unit) {
        val jsonString = preferences.getString(Constants.CURRENCY, "")
        val token = object : TypeToken<HashMap<String, Double>>() {}.type
        if (jsonString.isNullOrEmpty().not()) {
            onFetchCurrency.invoke(Gson().fromJson(jsonString, token))
        }
    }

    companion object {
        private var instance: CacheUtils? = null

        @JvmStatic
        fun get(context: Context): CacheUtils? {
            if (instance != null) return instance
            instance = CacheUtils(
                context.getSharedPreferences(
                    Constants.PREFERENCES,
                    Context.MODE_PRIVATE
                )
            )
            return instance
        }
    }
}