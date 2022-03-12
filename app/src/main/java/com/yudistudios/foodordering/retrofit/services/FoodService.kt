package com.yudistudios.foodordering.retrofit.services

import com.yudistudios.foodordering.retrofit.ApiUtils.Companion.GET_ALL_FOOD
import com.yudistudios.foodordering.retrofit.models.GetFoodsResponse
import retrofit2.Response
import retrofit2.http.GET


interface FoodService {
    @GET(GET_ALL_FOOD)
    suspend fun getAllFoods(): Response<GetFoodsResponse>
}