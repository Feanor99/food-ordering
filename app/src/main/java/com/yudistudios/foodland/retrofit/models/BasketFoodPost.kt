package com.yudistudios.foodland.retrofit.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BasketFoodPost(

    @SerializedName("yemek_adi")
    @Expose
    val foodName: String,

    @SerializedName("yemek_resim_adi")
    @Expose
    val foodImageName: String,

    @SerializedName("yemek_fiyat")
    @Expose
    val foodPrice: Int,

    @SerializedName("yemek_siparis_adet")
    @Expose
    var foodAmount: Int,

    @SerializedName("kullanici_adi")
    @Expose
    val userId: String,
)