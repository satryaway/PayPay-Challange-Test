package com.satryaway.paypaychallenge

import com.satryaway.paypaychallenge.utils.StringUtils
import org.junit.Test

class StringUtilsTest {

    @Test
    fun `currency trim must return true value`() {
        // Given
        val currency = "USDJPY"
        val expectedResult = "JPY"

        // When
        val trimmedCurrency = StringUtils.getCurrencyInitial(currency)

        // Then
        assert(expectedResult == trimmedCurrency)
    }

    @Test
    fun `currency trim must return original value when structured by less than 3 letters`() {
        // Given
        val currency = "IDR"
        val expectedResult = "IDR"

        // When
        val trimmedCurrency = StringUtils.getCurrencyInitial(currency)

        // Then
        assert(expectedResult == trimmedCurrency)
    }

    @Test
    fun `get currency initial is correct`() {
        // Given
        val currency = "IDR;14037"
        val isKey = true

        // When
        val currencyValue = StringUtils.getCurrencyValue(currency, isKey)

        // Then
        assert(currencyValue == "IDR")
    }

    @Test
    fun `get rate is correct`() {
        // Given
        val currency = "IDR;14037"
        val isKey = false

        // When
        val rate = StringUtils.getCurrencyValue(currency, isKey)

        // Then
        assert(rate == "14037")
    }

    @Test
    fun `passing wrong format will return`() {
        // Given
        val currency = "IDR14037"
        val isKey = true

        // When
        val currencyValue = StringUtils.getCurrencyValue(currency, isKey)

        // Then
        assert(currencyValue == currency)
    }

    @Test
    fun `the whole list of map will return correct array list`() {
        // Given
        val maps = hashMapOf(
            Pair("USD", 14050f),
            Pair("IDR", 130.90f)
        )

        // When
        val listOfCurrency = StringUtils.getCurrenciesValue(maps)

        // Then
        assert(listOfCurrency.contains("USD"))
        assert(listOfCurrency.contains("IDR"))
    }
}