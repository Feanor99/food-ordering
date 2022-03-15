package com.yudistudios.foodland.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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

    @Transient
    var amount: Int,

    @Transient
    var isFavorite: Boolean
) : Parcelable

fun Food.clone(): Food {
    return Food(
        id, name, imageName, price, amount, isFavorite
    )
}