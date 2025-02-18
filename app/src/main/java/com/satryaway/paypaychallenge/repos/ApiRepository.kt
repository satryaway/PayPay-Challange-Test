package com.satryaway.paypaychallenge.repos

import com.satryaway.paypaychallenge.utils.Api
import com.satryaway.paypaychallenge.utils.ApiService
import com.satryaway.paypaychallenge.utils.Constants
import retrofit2.await

class ApiRepository {
    var client: ApiService = Api.webService

    suspend fun live() = client.getLiveCurrency(Constants.access_key).await()

    suspend fun list() = client.getListCurrency(Constants.access_key).await()
}