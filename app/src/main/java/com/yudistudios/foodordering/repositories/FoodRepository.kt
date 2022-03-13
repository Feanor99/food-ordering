package com.yudistudios.foodordering.repositories

import androidx.lifecycle.LiveData
import com.yudistudios.foodordering.firebase.AuthUtils
import com.yudistudios.foodordering.firebase.DatabaseUtils
import com.yudistudios.foodordering.models.BasketFood
import com.yudistudios.foodordering.models.Food
import com.yudistudios.foodordering.retrofit.models.GetFoodsResponse
import com.yudistudios.foodordering.retrofit.services.FoodService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class FoodRepository @Inject constructor(private val foodService: FoodService) {

    val foodsInBasket: LiveData<MutableList<BasketFood>>
        get() = DatabaseUtils.getInstance().foodsInBasket

    val favoriteFoods: LiveData<List<String>>
        get() = DatabaseUtils.getInstance().favoriteFoods

    fun addFoodToBasket(food: Food) {
        val foodsExist = foodsInBasket.value ?: mutableListOf()
        foodsExist.let {
            if (it.any { fb -> fb.id == food.id.toInt() }) {
                val foodBasket = it.find { fb ->
                    fb.id == food.id.toInt()
                }

                foodBasket?.let {
                    foodBasket.foodAmount += food.amount
                    DatabaseUtils.getInstance().addFoodToBasket(foodBasket)
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
                DatabaseUtils.getInstance().addFoodToBasket(foodBasket)
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
                        DatabaseUtils.getInstance().removeFoodFromBasket(foodBasket)

                    } else {
                        DatabaseUtils.getInstance().addFoodToBasket(foodBasket)

                    }
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

        DatabaseUtils.getInstance().addFoodToBasket(foodBasket)
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

        DatabaseUtils.getInstance().removeFoodFromBasket(foodBasket)

    }

    fun clearAll() {
        DatabaseUtils.getInstance().clearBasket()
    }

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

    fun updateFavorites(ids: List<String>) {
        DatabaseUtils.getInstance().setFavoriteFoods(ids)
    }

}