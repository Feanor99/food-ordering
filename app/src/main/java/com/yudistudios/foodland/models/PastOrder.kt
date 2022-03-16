package com.yudistudios.foodland.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PastOrder(
    val date: Long,
    val items: List<BasketFood>
) : Parcelable