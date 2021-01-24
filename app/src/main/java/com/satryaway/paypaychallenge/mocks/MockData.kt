package com.satryaway.paypaychallenge.mocks

import com.satryaway.paypaychallenge.models.CurrencyModel
import com.satryaway.paypaychallenge.models.LiveModel
import java.util.*

object MockData {
    fun getLiveMock(): LiveModel {
        val maps = hashMapOf(
            Pair("USD", 14050.0),
            Pair("IDR", 130.90)
        )
        val currencyMap = TreeMap(maps)
        return LiveModel(
            true,
            currencyMap,
            null
        )
    }

    fun getCurrencyMock(): CurrencyModel {
        val maps = hashMapOf(
            Pair("USD", "United States Dollar"),
            Pair("IDR", "Indonesian Rupiah")
        )
        return CurrencyModel(
            true,
            maps,
            null
        )
    }
}