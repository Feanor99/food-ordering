package com.yudistudios.foodordering.ui.activities.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yudistudios.foodordering.repositories.FoodRepository
import com.yudistudios.foodordering.retrofit.models.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FoodDetailViewModel @Inject constructor(private val foodRepository: FoodRepository) :
    ViewModel() {

    val food = MutableLiveData<Food>()
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
}