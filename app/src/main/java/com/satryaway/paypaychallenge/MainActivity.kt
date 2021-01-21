package com.satryaway.paypaychallenge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.satryaway.paypaychallenge.repos.LiveRepository
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.await

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_call.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val result = LiveRepository().live().await()
                Log.d("result", result.source.orEmpty())
            }
        }
    }
}