package com.yudistudios.foodordering.repositories

import androidx.lifecycle.MutableLiveData
import com.yudistudios.foodordering.firebase.AuthUtils
import com.yudistudios.foodordering.firebase.DatabaseUtils
import com.yudistudios.foodordering.retrofit.models.AllFoodsResponse
import com.yudistudios.foodordering.retrofit.models.Food
import com.yudistudios.foodordering.retrofit.models.FoodBasket
import com.yudistudios.foodordering.retrofit.models.toFood
import com.yudistudios.foodordering.retrofit.services.FoodService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class FoodRepository @Inject constructor(private val foodService: FoodService) {

    val foods = MutableLiveData<List<Food>>()
    val foodsInBasket get() = DatabaseUtils.getInstance().foodsInBasket

    var getAllFoodsResponseCode: Int = -1

    lateinit var initialSortFoods: List<Food>

    fun addFoodToBasket(food: Food) {
        val foodsExist = foodsInBasket.value ?: mutableListOf()
        foodsExist.let {
            if (it.any { fb -> fb.foodId == food.id.toInt() }) {
                val foodBasket = it.find { fb ->
                    fb.foodId == food.id.toInt()
                }

                foodBasket?.let {
                    foodBasket.foodAmount += food.amount
                    if (foodBasket.foodAmount == 0) {
                        updateRemovedItemAmount(foodBasket.foodId)
                        DatabaseUtils.getInstance().removeFoodToBasket(foodBasket)
                    } else {
                        DatabaseUtils.getInstance().addFoodToBasket(foodBasket)
                    }
                }

            } else {
                val foodBasket = FoodBasket(
                    foodId = food.id.toInt(),
                    foodName = food.name,
                    foodImageName = food.imageName,
                    foodPrice = food.price.toInt(),
                    foodAmount = food.amount,
                    userId = AuthUtils.user!!.uid
                )

                updateInsertedItemAmount(foodBasket.foodId)
                DatabaseUtils.getInstance().addFoodToBasket(foodBasket)

            }
        }
    }

    fun updateAmounts(basket: List<FoodBasket>) {
        val currentFoods: MutableList<Food> = foods.value?.toMutableList() ?: mutableListOf()

        for (i in basket.indices) {
            val food = currentFoods.find {
                it.id.toInt() == basket[i].foodId
            }
            val index = currentFoods.indexOf(food)
            food?.let {
                currentFoods[index] = basket[i].toFood()
            }
        }

        foods.value = currentFoods.toList()
    }

    private fun updateRemovedItemAmount(id: Int) {
        val currentFoods: MutableList<Food> = foods.value?.toMutableList() ?: mutableListOf()

        val food = currentFoods.find {
            it.id.toInt() == id
        }
        val index = currentFoods.indexOf(food)
        food?.let {
            currentFoods[index] = Food(
                currentFoods[index].id,
                currentFoods[index].name,
                currentFoods[index].imageName,
                currentFoods[index].price,
                0
            )
        }

        foods.value = currentFoods.toList()
    }

    private fun updateInsertedItemAmount(id: Int) {
        val currentFoods: MutableList<Food> = foods.value?.toMutableList() ?: mutableListOf()

        val food = currentFoods.find {
            it.id.toInt() == id
        }
        val index = currentFoods.indexOf(food)
        food?.let {
            currentFoods[index] = Food(
                currentFoods[index].id,
                currentFoods[index].name,
                currentFoods[index].imageName,
                currentFoods[index].price,
                2
            )
        }

        foods.value = currentFoods.toList()
    }

    fun getAllFoods() {
        foodService.getAllFoods().enqueue(object : Callback<AllFoodsResponse> {
            override fun onResponse(
                call: Call<AllFoodsResponse>,
                response: Response<AllFoodsResponse>
            ) {
                getAllFoodsResponseCode = response.code()
                foods.value = response.body()?.foods ?: listOf()
                initialSortFoods = foods.value ?: listOf()
            }

            override fun onFailure(call: Call<AllFoodsResponse>, t: Throwable) {
                Timber.e("Error while get all foods: ${t.message}")
            }
        })
    }

    fun sortFoodsASC() {
        foods.value?.let { it ->
            foods.value = it.sortedWith(compareBy {
                it.price.toDouble()
            })
        }
    }

    fun sortFoodsDESC() {
        foods.value?.let { it ->
            foods.value = it.sortedWith(compareBy {
                it.price.toDouble()
            }).reversed()
        }
    }

    fun resetPriceSort() {
        foods.value = initialSortFoods
    }
}