package com.satryaway.paypaychallenge.models

data class CurrencyModel (
    val success: Boolean,
    val currencies: HashMap<String, String>?
)