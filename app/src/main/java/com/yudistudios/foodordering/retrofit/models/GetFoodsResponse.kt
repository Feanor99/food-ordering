package com.yudistudios.foodordering.retrofit.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.yudistudios.foodordering.models.Food

data class GetFoodsResponse(
    @SerializedName("yemekler")
    @Expose
    val foods: List<Food>,

    @SerializedName("success")
    @Expose
    val successCode: Int,
)