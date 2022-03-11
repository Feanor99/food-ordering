package com.yudistudios.foodordering.ui.activities.basket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yudistudios.foodordering.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BasketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)
    }
}