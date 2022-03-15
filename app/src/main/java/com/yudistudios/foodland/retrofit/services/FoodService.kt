package com.yudistudios.foodland.retrofit.services

import com.yudistudios.foodland.retrofit.ApiUtils.Companion.GET_ALL_FOOD
import com.yudistudios.foodland.retrofit.models.GetFoodsResponse
import retrofit2.Response
import retrofit2.http.GET


interface FoodService {
    @GET(GET_ALL_FOOD)
    suspend fun getAllFoods(): Response<GetFoodsResponse>
}