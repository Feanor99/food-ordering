package com.yudistudios.foodland.retrofit.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BasketResponse(
    @SerializedName("success")
    @Expose
    val success: Long,

    @SerializedName("message")
    @Expose
    val message: String,
)