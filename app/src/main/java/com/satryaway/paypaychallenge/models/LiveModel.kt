package com.satryaway.paypaychallenge.models

data class LiveModel (
    val success: Boolean?,
    val source: String?,
    val quotes: HashMap<String, Double>?
)