package com.yudistudios.foodordering.retrofit.services

import com.yudistudios.foodordering.retrofit.ApiUtils.Companion.GET_ALL_FOOD
import com.yudistudios.foodordering.retrofit.models.AllFoodsResponse
import retrofit2.Call
import retrofit2.http.GET


interface FoodService {
    @GET(GET_ALL_FOOD)
    fun getAllFoods(): Call<AllFoodsResponse>
}