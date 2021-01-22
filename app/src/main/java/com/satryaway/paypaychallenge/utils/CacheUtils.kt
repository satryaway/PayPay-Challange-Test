package com.satryaway.paypaychallenge.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CacheUtils(pref: SharedPreferences) {

    private val preferences = pref

    fun isCurrencyExpired(): Boolean {
        // add time condition here
        return preferences.getString(Constants.CURRENCY, "").isNullOrEmpty()
    }

    fun saveCurrencies(quotes: HashMap<String, Float>?, onSave: (HashMap<String, Float>,Boolean) -> Unit) {
        if (quotes != null) {
            val currencyMap = hashMapOf<String, Float>()
            quotes.forEach {
                val currency = StringUtils.getCurrencyInitial(it.key)
                currencyMap[currency] = it.value
            }
            val jsonString = Gson().toJson(currencyMap)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString(Constants.CURRENCY, jsonString)
            editor.apply()
            onSave.invoke(currencyMap, true)
        } else {
            onSave.invoke(hashMapOf(), false)
        }
    }

    fun initCurrencies(onFetchCurrency: (HashMap<String, Float>) -> Unit){
        val jsonString = preferences.getString(Constants.CURRENCY, "")
        val token = object : TypeToken<HashMap<String, Float>>() {}.type
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