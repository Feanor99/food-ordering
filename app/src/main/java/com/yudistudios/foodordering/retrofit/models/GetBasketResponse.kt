package com.yudistudios.foodordering.retrofit.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetBasketResponse(
    @SerializedName("sepet_yemekler")
    @Expose
    val foods: List<BasketFood>,

    @SerializedName("success")
    @Expose
    val successCode: Int,
)
