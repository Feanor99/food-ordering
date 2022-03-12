package com.yudistudios.foodordering.retrofit.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetFoodsResponse(
    @SerializedName("yemekler")
    @Expose
    val foods: List<Food>,

    @SerializedName("success")
    @Expose
    val successCode: Int,
)