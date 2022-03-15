package com.yudistudios.foodland.retrofit.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.yudistudios.foodland.models.BasketFood

data class GetBasketResponse(
    @SerializedName("sepet_yemekler")
    @Expose
    val foods: List<BasketFood>,

    @SerializedName("success")
    @Expose
    val successCode: Int,
)
