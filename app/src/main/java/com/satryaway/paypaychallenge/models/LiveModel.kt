package com.satryaway.paypaychallenge.models

import java.util.*

data class LiveModel (
    val success: Boolean?,
    val source: String?,
    val quotes: TreeMap<String, Double>?
)