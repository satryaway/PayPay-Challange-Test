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
}