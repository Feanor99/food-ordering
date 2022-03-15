package com.yudistudios.foodland.ui.activities.basket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yudistudios.foodland.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BasketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)
    }
}