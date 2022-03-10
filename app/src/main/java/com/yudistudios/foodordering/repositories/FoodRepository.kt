package com.yudistudios.foodordering.repositories

import com.yudistudios.foodordering.firebase.AuthUtils
import com.yudistudios.foodordering.firebase.DatabaseUtils
import com.yudistudios.foodordering.retrofit.models.Food
import com.yudistudios.foodordering.retrofit.models.FoodBasket
import com.yudistudios.foodordering.retrofit.services.FoodService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FoodRepository @Inject constructor(private val foodService: FoodService) {

    val foodsInBasket get() = DatabaseUtils.getInstance().foodsInBasket

    val lastRemovedFoodIdFromBasket get() = DatabaseUtils.getInstance().lastRemovedFoodIdFromBasket

    fun addFoodToBasket(food: Food) {
        val foodsExist = foodsInBasket.value ?: mutableListOf()
        foodsExist.let {
            if (it.any { fb -> fb.foodId == food.id.toInt() }) {
                val foodBasket = it.find { fb ->
                    fb.foodId == food.id.toInt()
                }

                foodBasket?.let {
                    foodBasket.foodAmount += food.amount
                    DatabaseUtils.getInstance().addFoodToBasket(foodBasket)
                }

            } else {
                val foodBasket = FoodBasket(
                    foodId = food.id.toInt(),
                    foodName = food.name,
                    foodImageName = food.imageName,
                    foodPrice = food.price.toInt(),
                    foodAmount = 1,
                    userId = AuthUtils.user!!.uid
                )
                DatabaseUtils.getInstance().addFoodToBasket(foodBasket)
            }
        }
    }

    fun removeFoodFromBasket(food: Food) {
        val foodsExist = foodsInBasket.value ?: mutableListOf()
        foodsExist.let {
            if (it.any { fb -> fb.foodId == food.id.toInt() }) {
                val foodBasket = it.find { fb ->
                    fb.foodId == food.id.toInt()
                }

                foodBasket?.let {
                    foodBasket.foodAmount += food.amount
                    if (foodBasket.foodAmount == 0) {
                        DatabaseUtils.getInstance().removeFoodFromBasket(foodBasket)

                    } else {
                        DatabaseUtils.getInstance().addFoodToBasket(foodBasket)

                    }
                }

            }
        }
    }


    fun setFoodToBasket(food: Food) {

        val foodBasket = FoodBasket(
            foodId = food.id.toInt(),
            foodName = food.name,
            foodImageName = food.imageName,
            foodPrice = food.price.toInt(),
            foodAmount = food.amount,
            userId = AuthUtils.user!!.uid
        )

        DatabaseUtils.getInstance().addFoodToBasket(foodBasket)
    }

    fun removeFoodPermanentlyFromBasket(food: Food) {

        val foodBasket = FoodBasket(
            foodId = food.id.toInt(),
            foodName = food.name,
            foodImageName = food.imageName,
            foodPrice = food.price.toInt(),
            foodAmount = food.amount,
            userId = AuthUtils.user!!.uid
        )

        DatabaseUtils.getInstance().removeFoodFromBasket(foodBasket)

    }


    fun getAllFoods(): Flow<List<Food>> {
        return flow {
            try {
                val response = foodService.getAllFoods()
                emit(response.body()?.foods ?: listOf())
            } catch (e: Exception) {
                emit(listOf())
            }

        }
    }

}