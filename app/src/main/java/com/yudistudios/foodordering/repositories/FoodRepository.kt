package com.yudistudios.foodordering.repositories

import androidx.lifecycle.MutableLiveData
import com.yudistudios.foodordering.retrofit.models.AllFoodsResponse
import com.yudistudios.foodordering.retrofit.models.Food
import com.yudistudios.foodordering.retrofit.services.FoodService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class FoodRepository @Inject constructor(private val foodService: FoodService) {

    val foods = MutableLiveData<List<Food>>()

    var getAllFoodsResponseCode: Int = -1

    fun getAllFoods(){
        foodService.getAllFoods().enqueue(object: Callback<AllFoodsResponse> {
            override fun onResponse(
                call: Call<AllFoodsResponse>,
                response: Response<AllFoodsResponse>
            ) {
                getAllFoodsResponseCode = response.code()
                foods.value = response.body()?.foods ?: listOf()
            }

            override fun onFailure(call: Call<AllFoodsResponse>, t: Throwable) {
                Timber.e("Error while get all foods: ${t.message}")
            }
        })
    }
}