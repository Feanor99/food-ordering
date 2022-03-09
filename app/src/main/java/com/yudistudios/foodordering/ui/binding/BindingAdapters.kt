package com.yudistudios.foodordering.ui.binding

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.yudistudios.foodordering.R
import java.text.DecimalFormat

@BindingAdapter("loadImage")
fun ImageView.loadImage(name: String) {
    val baseUrl = "http://kasimadalan.pe.hu/yemekler/resimler/"
    Glide.with(this.rootView).load("$baseUrl$name").placeholder(R.drawable.loading_anim).into(this)
}

@BindingAdapter("priceText")
fun TextView.setPriceText(price: String) {
    val currency = "₺"
    val priceDouble = price.toDoubleOrNull()
    val formatter = DecimalFormat("###,###.00")
    if (priceDouble != null) {
        text = String.format("%s %s", formatter.format(priceDouble), currency)
    }
}