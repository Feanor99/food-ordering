package com.yudistudios.foodland.ui.activities.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yudistudios.foodland.models.Food
import com.yudistudios.foodland.repositories.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FoodDetailViewModel @Inject constructor(private val foodRepository: FoodRepository) :
    ViewModel() {

    val food = MutableLiveData<Food>()

    val favoriteFoods get() = foodRepository.favoriteFoods

    val increaseButtonIsClicked = MutableLiveData(false)
    val decreaseButtonIsClicked = MutableLiveData(false)

    fun addFoodToBasket() {
        food.value?.let {
            val foodTemp = food.value!!
            foodTemp.amount = 1
            food.value = foodTemp
        }
    }

    fun increaseFoodAmountOnClick() {
        increaseButtonIsClicked.value = true
    }

    fun decreaseFoodAmountOnClick() {
        decreaseButtonIsClicked.value = true
    }

    fun updateFoodInBasket() {
        food.value?.let {
            if (food.value!!.amount <= 0) {
                foodRepository.removeFoodPermanentlyFromBasket(food.value!!)
            } else {
                foodRepository.setFoodToBasket(food.value!!)
            }
        }
    }

    fun favoriteOnClick() {
        val favorites = favoriteFoods.value?.toMutableList()
        val foodTemp = food.value
        if (favorites != null && foodTemp != null) {
            if (favorites.contains(foodTemp.id)) {
                favorites.remove(foodTemp.id)
            } else {
                favorites.add(foodTemp.id)
            }
            updateFavorites(favorites)

            foodTemp.isFavorite = !foodTemp.isFavorite

            food.value = foodTemp!!
        }
    }

    private fun updateFavorites(ids: MutableList<String>) {
        foodRepository.updateFavorites(ids.toList())
    }

}