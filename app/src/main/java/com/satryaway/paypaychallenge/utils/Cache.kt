package com.satryaway.paypaychallenge.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Cache (pref: SharedPreferences) {

    val preferences = pref

    fun isCurrenciesExisted() : Boolean {
        return preferences.getString(Constants.QUOTE, "").isNullOrEmpty().not()
    }

    fun saveCurrencies(quotes: HashMap<String, Float>?) {
        quotes.let {
            val jsonString = Gson().toJson(it)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString(Constants.QUOTE, jsonString)
            editor.apply()
        }
    }

    fun getCurrencies() : HashMap<String, Float> {
        val jsonString = preferences.getString(Constants.QUOTE, "")
        val token = object : TypeToken<HashMap<String, Float>>(){}.type
        var listOfCurrency = hashMapOf<String, Float>()
        if (jsonString.isNullOrEmpty().not()) {
            listOfCurrency = Gson().fromJson(jsonString, token)
        }

        return StringUtils.modifyCurrencyName(listOfCurrency)
    }

    companion object {
        private var instance: Cache? = null

        @JvmStatic
        fun get(context: Context): Cache? {
            if (instance != null) return instance
            instance = Cache(context.getSharedPreferences(Constants.PREFERENCES,
                Context.MODE_PRIVATE))
            return instance
        }
    }
}