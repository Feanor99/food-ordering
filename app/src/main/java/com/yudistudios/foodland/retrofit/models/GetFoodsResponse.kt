package com.yudistudios.foodland.retrofit.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.yudistudios.foodland.models.Food

data class GetFoodsResponse(
    @SerializedName("yemekler")
    @Expose
    val foods: List<Food>,

    @SerializedName("success")
    @Expose
    val successCode: Int,
)