package com.yudistudios.foodland.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val date: Long,
    val items: List<BasketFood>,
    val longitude: Double,
    val latitude: Double,
) : Parcelable