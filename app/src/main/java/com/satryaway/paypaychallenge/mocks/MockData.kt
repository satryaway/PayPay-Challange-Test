package com.satryaway.paypaychallenge.mocks

import com.satryaway.paypaychallenge.models.CurrencyModel
import com.satryaway.paypaychallenge.models.ErrorModel
import com.satryaway.paypaychallenge.models.LiveModel
import java.util.*

object MockData {
    fun getLiveMock(): LiveModel {
        val maps = hashMapOf(Pair("USD", 14050.0), Pair("IDR", 130.90))
        val currencyMap = TreeMap(maps)

        return LiveModel(true, currencyMap, null)
    }

    fun getLiveMockFailed(): LiveModel {
        val errorMsg = "Your monthly usage limit has been reached. " +
                "Please upgrade your subscription plan."

        return LiveModel(success = false, quotes = null, error = ErrorModel(errorMsg))
    }

    fun getCurrencyMock(): CurrencyModel {
        val maps = hashMapOf(
            Pair("USD", "United States Dollar"),
            Pair("IDR", "Indonesian Rupiah")
        )
        return CurrencyModel(true, maps, null)
    }

    fun getCurrencyMockFailed(): CurrencyModel {
        val errorMsg = "Your account is not active. Please get in touch with Customer Support."

        return CurrencyModel(success = false, currencies = null, error = ErrorModel(errorMsg))
    }
}