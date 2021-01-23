package com.satryaway.paypaychallenge

import com.satryaway.paypaychallenge.presenters.ConvertPresenter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ConvertPresenterTest {
    private val presenter = ConvertPresenter()
    private lateinit var view: ConvertPresenter.View

    @Before
    fun setup() {
        view = mock(ConvertPresenter.View::class.java)
    }

    @Test
    fun `convert correct nominal will return correct value`() {
        // Given
        val mockNominal = "135"
        presenter.attachView(view)

        // When
        presenter.convert(mockNominal)

        // Then
        verify(view).setConversionValue()
    }

    @Test
    fun `convert false nominal will return correct value`() {
        // Given
        val mockNominal = "a1b2c3"
        presenter.attachView(view)

        // When
        presenter.convert(mockNominal)

        // Then
        verify(view).showErrorMessage("Please Input Numeric Value")
    }

    @Test
    fun `convert empty will return correct value`() {
        // Given
        val mockNominal = ""
        presenter.attachView(view)

        // When
        presenter.convert(mockNominal)

        // Then
        verify(view).showErrorMessage("Please Input Numeric Value")
    }

    @Test
    fun `convert zero will return correct value`() {
        // Given
        val mockNominal = "0"
        presenter.attachView(view)

        // When
        presenter.convert(mockNominal)

        // Then
        verify(view).showErrorMessage("Please Input Correct Value")
    }

    @Test
    fun `map will return correct list of currency`() {
        // Given
        val currencyInitial = "IDR"
        val currencyRate = 14030.0
        presenter.currencyMap[currencyInitial] = currencyRate

        // When
        val mockList = presenter.getCollectedList()

        // Then
        assert(mockList.contains("$currencyInitial;$currencyRate"))
    }


    @Test
    fun `map will return correct rate`() {
        // Given
        val currencyInitial = "IDR"
        val currencyRate = 14030.0
        presenter.currencyMap[currencyInitial] = currencyRate
        presenter.currentCurrency = currencyInitial

        // When
        val mockRate = presenter.getSourceRate()

        // Then
        assert(mockRate.equals(currencyRate))
    }
}