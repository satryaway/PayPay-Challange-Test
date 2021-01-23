package com.satryaway.paypaychallenge.models

import java.util.*

data class LiveModel (
    val success: Boolean?,
    val quotes: TreeMap<String, Double>?
)