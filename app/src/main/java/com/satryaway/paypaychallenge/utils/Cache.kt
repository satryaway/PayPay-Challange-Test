package com.satryaway.paypaychallenge.utils

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Cache(pref: SharedPreferences) {

    private val preferences = pref
    var currencyMap = hashMapOf<String, Float>()

    fun isCurrencyExpired(): Boolean {
        // add time condition here
        return preferences.getString(Constants.CURRENCY, "").isNullOrEmpty()
    }

    fun saveCurrencies(quotes: HashMap<String, Float>?, onSave: (Boolean) -> Unit) {
        if (quotes != null) {
            currencyMap.clear()
            quotes.forEach {
                val currency = StringUtils.getCurrencyInitial(it.key)
                currencyMap[currency] = it.value
            }
            val jsonString = Gson().toJson(currencyMap)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString(Constants.CURRENCY, jsonString)
            editor.apply()
            onSave.invoke(true)
        } else {
            onSave.invoke(false)
        }
    }

    fun initCurrencies(){
        val jsonString = preferences.getString(Constants.CURRENCY, "")
        val token = object : TypeToken<HashMap<String, Float>>() {}.type
        if (jsonString.isNullOrEmpty().not()) {
            currencyMap = Gson().fromJson(jsonString, token)
        }
    }

    companion object {
        private var instance: Cache? = null

        @JvmStatic
        fun get(context: Context): Cache? {
            if (instance != null) return instance
            instance = Cache(
                context.getSharedPreferences(
                    Constants.PREFERENCES,
                    Context.MODE_PRIVATE
                )
            )
            return instance
        }
    }
}