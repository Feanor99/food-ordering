package com.yudistudios.foodordering.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.yudistudios.foodordering.retrofit.models.FoodBasketPost

data class BasketFood(
    @SerializedName("sepet_yemek_id")
    @Expose
    val id: Int,

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

fun BasketFood.toFood(): Food {
    return Food(
        id = id.toString(),
        name = foodName,
        imageName = foodImageName,
        price = foodPrice.toString(),
        amount = foodAmount
    )
}

fun BasketFood.toFoodBasketPost(): FoodBasketPost {
    return FoodBasketPost(
        foodName = foodName,
        foodImageName = foodImageName,
        foodPrice = foodPrice,
        foodAmount = foodAmount,
        userId = userId
    )
}