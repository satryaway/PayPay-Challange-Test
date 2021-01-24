package com.satryaway.paypaychallenge.utils

import com.satryaway.paypaychallenge.models.CurrencyModel
import com.satryaway.paypaychallenge.models.LiveModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("live")
    fun getLiveCurrency(
        @Query("access_key") status: String
    ): Call<LiveModel?>

    @GET("list")
    fun getListCurrency(
        @Query("access_key") status: String
    ): Call<CurrencyModel?>
}