package com.satryaway.paypaychallenge.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.HashMap

class CacheUtils(pref: SharedPreferences) {

    private val preferences = pref

    fun isCurrencyExpired(): Boolean {
        val isCacheEmpty = preferences.getString(Constants.CURRENCY, "").isNullOrEmpty()
        val timeFlag = Date(preferences.getLong(Constants.TIME_FLAG, 0))
        val isExpired = isMoreThanDesignatedTimeToFetchCurrency(timeFlag)

        return isExpired || isCacheEmpty
    }

    fun isMoreThanDesignatedTimeToFetchCurrency(timeFlag: Date): Boolean {
        val timeNow: Long = Calendar.getInstance().time.time
        val flag = timeFlag.time
        val subs = timeNow - flag
        return subs >= Constants.RANGE_BETWEEN_LAST_CURRENCY_FETCH
    }

    fun saveCurrencies(
        quotes: TreeMap<String, Double>?,
        currencyList: HashMap<String, String>?,
        onSave: (TreeMap<String, Double>, HashMap<String, String>, Boolean) -> Unit
    ) {
        if (quotes != null && currencyList != null) {
            val currencyMap = TreeMap<String, Double>()
            quotes.forEach {
                val currency = StringUtils.getCurrencyInitial(it.key)
                currencyMap[currency] = it.value
            }
            val jsonString = Gson().toJson(currencyMap)
            val jsonCurrencyName = Gson().toJson(currencyList)
            val editor: SharedPreferences.Editor = preferences.edit()

            editor.putString(Constants.CURRENCY_NAME, jsonCurrencyName)
            editor.putString(Constants.CURRENCY, jsonString)
            editor.apply()

            val timeNow = Calendar.getInstance().time
            preferences.edit().putLong(Constants.TIME_FLAG, timeNow.time).apply()

            onSave.invoke(currencyMap, currencyList, true)
        } else {
            onSave.invoke(TreeMap(), hashMapOf(), false)
        }
    }

    fun initCurrencies(onFetchCurrency: (TreeMap<String, Double>, HashMap<String, String>) -> Unit) {
        val jsonString = preferences.getString(Constants.CURRENCY, "")
        val jsonCurrencyName = preferences.getString(Constants.CURRENCY_NAME, "")
        val token = object : TypeToken<TreeMap<String, Double>>() {}.type
        val tokenCurrencyName = object : TypeToken<HashMap<String, String>>() {}.type
        val currencyMap: TreeMap<String, Double> = Gson().fromJson(jsonString, token)
        val currencyNameMap: HashMap<String, String> = Gson()
            .fromJson(jsonCurrencyName, tokenCurrencyName)
        if (jsonString.isNullOrEmpty().not()) {
            onFetchCurrency.invoke(currencyMap, currencyNameMap)
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