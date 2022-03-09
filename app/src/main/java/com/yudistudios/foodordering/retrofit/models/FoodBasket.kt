package com.yudistudios.foodordering.retrofit.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FoodBasket(
    @SerializedName("sepet_yemek_id")
    @Expose
    val foodId: Int,

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

fun FoodBasket.toFood(): Food {
    return Food(
        id = foodId.toString(),
        name = foodName,
        imageName = foodImageName,
        price = foodPrice.toString(),
        amount = foodAmount
    )
}