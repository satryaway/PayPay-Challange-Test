package com.satryaway.paypaychallenge

import android.content.Context
import android.content.SharedPreferences
import com.satryaway.paypaychallenge.mocks.MockData
import com.satryaway.paypaychallenge.models.CurrencyModel
import com.satryaway.paypaychallenge.models.LiveModel
import com.satryaway.paypaychallenge.presenters.ConvertPresenter
import com.satryaway.paypaychallenge.utils.CacheUtils
import com.satryaway.paypaychallenge.utils.Constants
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class ConvertPresenterTest {
    private val presenter = ConvertPresenter()
    private lateinit var view: ConvertPresenter.View
    private lateinit var cacheUtils: CacheUtils
    private val sharedPrefs = mock(SharedPreferences::class.java)
    private val mockEditor = mock(SharedPreferences.Editor::class.java)
    private val context = mock(Context::class.java)

    @Before
    fun setup() {
        view = mock(ConvertPresenter.View::class.java)
        cacheUtils = CacheUtils(sharedPrefs)
        presenter.attachView(view)
    }

    @Test
    fun `convert correct nominal will return correct value`() {
        // Given
        val mockNominal = "135"

        // When
        presenter.convert(mockNominal)

        // Then
        verify(view).setConversionValue()
    }

    @Test
    fun `convert false nominal will return correct value`() {
        // Given
        val mockNominal = "a1b2c3"

        // When
        presenter.convert(mockNominal)

        // Then
        verify(view).showErrorMessage("Please Input Numeric Value")
    }

    @Test
    fun `convert empty will return correct value`() {
        // Given
        val mockNominal = ""

        // When
        presenter.convert(mockNominal)

        // Then
        verify(view).showErrorMessage("Please Input Numeric Value")
    }

    @Test
    fun `convert zero will return correct value`() {
        // Given
        val mockNominal = "0"

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

    @Test
    fun `successful handle request will return correct value`() {
        // Given
        `when`(sharedPrefs.edit()).thenReturn(mockEditor)
        val mockLive = MockData.getLiveMock()
        val mockCurrency = MockData.getCurrencyMock()
        `when`(mockEditor.putLong(anyString(), anyLong())).thenReturn(mockEditor)

        // When
        presenter.handleRequestRate(cacheUtils, mockLive, mockCurrency)

        // Then
        verify(view).onFetchedCurrency(arrayListOf("IDR", "USD"))
    }

    @Test
    fun `failed request will return correct information`() {
        // Given
        val maps = hashMapOf(Pair("IDR", 130.90), Pair("USD", 14050.0))
        val currencyMap = TreeMap(maps)
        val mockLive = LiveModel(success = true, quotes = currencyMap, error = null)
        val mockCurrency: CurrencyModel? = null

        // When
        presenter.handleRequestRate(cacheUtils, mockLive, mockCurrency)

        // Then
        verify(view).onFailedFetchingCurrency("Unknown Error")
    }

    @Test
    fun `failed request currency will return correct information`() {
        // Given
        val mockLive = MockData.getLiveMockFailed()
        val mockCurrency: CurrencyModel? = null

        // When
        presenter.handleRequestRate(cacheUtils, mockLive, mockCurrency)

        // Then
        verify(view).onFailedFetchingCurrency("Unknown Error")
    }

    @Test
    fun `failed currency request handle request will return correct value`() {
        // Given
        val mockLive = MockData.getLiveMockFailed()
        val mockCurrency = MockData.getCurrencyMock()

        // When
        presenter.handleRequestRate(cacheUtils, mockLive, mockCurrency)

        // Then
        verify(view).onFailedFetchingCurrency(mockLive.error?.info ?: "")
    }

    @Test
    fun `failed currency name request handle request will return correct value`() {
        // Given
        val currencyMap = MockData.getLiveMock().quotes
        val mockLive = LiveModel(success = true, quotes = currencyMap, error = null)
        val mockCurrency = MockData.getCurrencyMockFailed()

        // When
        presenter.handleRequestRate(cacheUtils, mockLive, mockCurrency)

        // Then
        verify(view).onFailedFetchingCurrency(mockCurrency.error?.info ?: "")
    }

    @Test
    fun `return data from cache if data is not expired`() {
        // Given
        val rangeBetweenLastFetch = 20
        val date = Date().time - (rangeBetweenLastFetch * 60 * 1000)
        val currencyList = arrayListOf("IDR", "USD")

        presenter.attachView(view)

        `when`(context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE))
            .thenReturn(sharedPrefs)
        `when`(sharedPrefs.getString(Constants.CURRENCY, ""))
            .thenReturn("{\"IDR\" : 14050.5, \"USD\" : 135.55}")
        `when`(sharedPrefs.getString(Constants.CURRENCY_NAME, ""))
            .thenReturn("{\"IDR\" : \"Indonesian Rupiah\", " +
                    "\"USD\" : \"United State Dollar\"}")
        `when`(sharedPrefs.getLong(Constants.TIME_FLAG, 0))
            .thenReturn(date)

        // When
        presenter.requestRate(context)

        // Then
        verify(view).onFetchedCurrency(currencyList)
    }
}