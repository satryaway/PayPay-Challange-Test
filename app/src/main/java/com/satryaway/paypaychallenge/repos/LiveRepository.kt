package com.satryaway.paypaychallenge.repos

import com.satryaway.paypaychallenge.utils.Api
import com.satryaway.paypaychallenge.utils.ApiService
import com.satryaway.paypaychallenge.utils.Constants

class LiveRepository {
    var client: ApiService = Api.webService

    fun live() = client.getLiveCurrency(Constants.access_key)
}