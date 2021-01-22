package com.satryaway.paypaychallenge.utils

import android.content.Context
import android.widget.Toast

object DialogUtils {

    fun showToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}