package com.yudistudios.foodland.repositories

import com.yudistudios.foodland.firebase.AuthUtils
import com.yudistudios.foodland.firebase.DatabaseUtils
import com.yudistudios.foodland.models.BasketFood
import com.yudistudios.foodland.models.Food
import com.yudistudios.foodland.retrofit.models.GetFoodsResponse
import com.yudistudios.foodland.retrofit.services.FoodService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class FoodRepository @Inject constructor(private val foodService: FoodService) {

    val foodsInBasket get() = DatabaseUtils.instance.foodsInBasket

    val favoriteFoods get() = DatabaseUtils.instance.favoriteFoods

    fun getAllFoods(): Flow<GetFoodsResponse> {
        return flow {
            try {
                val response = foodService.getAllFoods()
                emit(response.body() ?: GetFoodsResponse(listOf(), 0))
            } catch (e: Exception) {
                Timber.e("${e.message}")
                emit(GetFoodsResponse(listOf(), 0))
            }
        }
    }

    fun addFoodToBasket(food: Food) {
        val foodsExist = foodsInBasket.value ?: mutableListOf()
        foodsExist.let {
            if (it.any { fb -> fb.id == food.id.toInt() }) {
                val foodBasket = it.find { fb ->
                    fb.id == food.id.toInt()
                }

                foodBasket?.let {
                    foodBasket.foodAmount += food.amount
                    DatabaseUtils.instance.addFoodToBasket(foodBasket)
                }

            } else {
                val foodBasket = BasketFood(
                    id = food.id.toInt(),
                    foodName = food.name,
                    foodImageName = food.imageName,
                    foodPrice = food.price.toInt(),
                    foodAmount = 1,
                    userId = AuthUtils.user!!.uid
                )
                DatabaseUtils.instance.addFoodToBasket(foodBasket)
            }
        }
    }

    fun removeFoodFromBasket(food: Food) {
        val foodsExist = foodsInBasket.value ?: mutableListOf()
        foodsExist.let {
            if (it.any { fb -> fb.id == food.id.toInt() }) {
                val foodBasket = it.find { fb ->
                    fb.id == food.id.toInt()
                }

                foodBasket?.let {
                    foodBasket.foodAmount += food.amount
                    if (foodBasket.foodAmount == 0) {
                        DatabaseUtils.instance.removeFoodFromBasket(foodBasket)

                    } else {
                        DatabaseUtils.instance.addFoodToBasket(foodBasket)

                    }
                }

            }
        }
    }

    fun deleteFoodFromBasket(food: Food) {
        val foodsExist = foodsInBasket.value ?: mutableListOf()
        foodsExist.let {
            if (it.any { fb -> fb.id == food.id.toInt() }) {
                val foodBasket = it.find { fb ->
                    fb.id == food.id.toInt()
                }

                foodBasket?.let {
                    DatabaseUtils.instance.removeFoodFromBasket(foodBasket)
                }
            }
        }
    }

    fun setFoodToBasket(food: Food) {

        val foodBasket = BasketFood(
            id = food.id.toInt(),
            foodName = food.name,
            foodImageName = food.imageName,
            foodPrice = food.price.toInt(),
            foodAmount = food.amount,
            userId = AuthUtils.user!!.uid
        )

        DatabaseUtils.instance.addFoodToBasket(foodBasket)
    }

    fun removeFoodPermanentlyFromBasket(food: Food) {

        val foodBasket = BasketFood(
            id = food.id.toInt(),
            foodName = food.name,
            foodImageName = food.imageName,
            foodPrice = food.price.toInt(),
            foodAmount = food.amount,
            userId = AuthUtils.user!!.uid
        )

        DatabaseUtils.instance.removeFoodFromBasket(foodBasket)

    }

    fun clearAll() {
        DatabaseUtils.instance.clearBasket()
    }

    fun updateFavorites(ids: List<String>) {
        DatabaseUtils.instance.setFavoriteFoods(ids)
    }

}