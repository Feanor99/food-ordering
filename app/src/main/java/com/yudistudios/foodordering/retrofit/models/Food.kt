package com.yudistudios.foodordering.retrofit.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Food(
    @SerializedName("yemek_id")
    @Expose
    val id: String,

    @SerializedName("yemek_adi")
    @Expose
    val name: String,

    @SerializedName("yemek_resim_adi")
    @Expose
    val imageName: String,

    @SerializedName("yemek_fiyat")
    @Expose
    val price: String,
)