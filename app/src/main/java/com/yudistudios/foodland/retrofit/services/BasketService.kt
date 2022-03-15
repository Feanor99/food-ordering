package com.yudistudios.foodland.retrofit.services

import com.yudistudios.foodland.retrofit.ApiUtils.Companion.ADD_FOOD_TO_BASKET
import com.yudistudios.foodland.retrofit.ApiUtils.Companion.GET_BASKET
import com.yudistudios.foodland.retrofit.ApiUtils.Companion.REMOVE_FOOD_FROM_BASKET
import com.yudistudios.foodland.retrofit.models.BasketResponse
import com.yudistudios.foodland.retrofit.models.GetBasketResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BasketService {
    @FormUrlEncoded
    @POST(GET_BASKET)
    suspend fun getBasket(
        @Field("kullanici_adi") userId: String,
    ): Response<GetBasketResponse>

    @FormUrlEncoded
    @POST(ADD_FOOD_TO_BASKET)
    suspend fun addFoodToBasket(
        @Field("yemek_adi") foodName: String,
        @Field("yemek_resim_adi") foodImageName: String,
        @Field("yemek_fiyat") foodPrice: Int,
        @Field("yemek_siparis_adet") foodAmount: Int,
        @Field("kullanici_adi") userId: String,
    ): Response<BasketResponse>

    @FormUrlEncoded
    @POST(REMOVE_FOOD_FROM_BASKET)
    suspend fun removeFoodFromBasket(
        @Field("sepet_yemek_id") basketFoodId: Int,
        @Field("kullanici_adi") userId: String,
    ): Response<BasketResponse>
}