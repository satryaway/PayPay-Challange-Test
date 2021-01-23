package com.satryaway.paypaychallenge

import android.content.Context
import android.content.SharedPreferences
import com.satryaway.paypaychallenge.utils.CacheUtils
import com.satryaway.paypaychallenge.utils.Constants
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*

class CacheUtilsTest {
    private val context: Context = mock(Context::class.java)
    private val sharedPrefs = mock(SharedPreferences::class.java)

    @Test
    fun `return false if time flag more than designated time`() {
        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs)
        val date = Date().time - (29 * 60 * 1000)

        // When
        val isMoreThanFlagTime =
            CacheUtils.get(context)?.isMoreThanDesignatedTimeToFetchCurrency(Date(date))

        // Then
        assert(isMoreThanFlagTime == false)
    }

    @Test
    fun `return true if time flag more than designated time`() {
        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs)
        val date = Date().time - (50 * 60 * 1000)

        // When
        val isMoreThanFlagTime =
            CacheUtils.get(context)?.isMoreThanDesignatedTimeToFetchCurrency(Date(date))

        // Then
        assert(isMoreThanFlagTime == true)
    }

    @Test
    fun `time must be expired when more than designated times`() {
        // Given
        mockPreference(40)

        // When
        val cacheUtils = CacheUtils(sharedPrefs)
        val isTimeExpired = cacheUtils.isCurrencyExpired()

        // Then
        assert(isTimeExpired)
    }
    @Test
    fun `time must not be expired when less than designated times`() {
        // Given
        mockPreference(20)

        // When
        val cacheUtils = CacheUtils(sharedPrefs)
        val isTimeExpired = cacheUtils.isCurrencyExpired()

        // Then
        assert(isTimeExpired.not())
    }

    private fun mockPreference(minutes: Int) {
        val date = Date().time - (minutes * 60 * 1000)
        val mockEditor = mock(SharedPreferences.Editor::class.java)

        `when`(context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE))
            .thenReturn(sharedPrefs)
        `when`(sharedPrefs.edit())
            .thenReturn(mockEditor)
        `when`(sharedPrefs.getString(Constants.CURRENCY, ""))
            .thenReturn("string")
        `when`(sharedPrefs.getLong(Constants.TIME_FLAG, 0))
            .thenReturn(date)
    }
}