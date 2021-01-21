package com.satryaway.paypaychallenge

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.satryaway.paypaychallenge.repos.LiveRepository
import com.satryaway.paypaychallenge.utils.Cache
import com.satryaway.paypaychallenge.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.await

class MainActivity : AppCompatActivity() {
    var liveRepository = LiveRepository()
    var cache: Cache? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cache = Cache.get(this)

        btn_call.setOnClickListener {
            cache?.apply {
                if (isCurrenciesExisted()) {
                    val quotes = getCurrencies()
                    Log.d("result", quotes.toString())
                } else {
                    GlobalScope.launch(Dispatchers.IO) {
                        val result = liveRepository.live()
                        saveCurrencies(result.quotes)
                        Log.d("result", result.quotes.toString())
                    }
                }
            }
        }
    }
}