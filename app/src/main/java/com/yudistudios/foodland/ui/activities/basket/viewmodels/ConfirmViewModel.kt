package com.yudistudios.foodland.ui.activities.basket.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yudistudios.foodland.firebase.DatabaseUtils
import com.yudistudios.foodland.repositories.BasketRepository
import com.yudistudios.foodland.repositories.FoodRepository
import com.yudistudios.foodland.models.BasketFood
import com.yudistudios.foodland.models.Food
import com.yudistudios.foodland.retrofit.models.GetBasketResponse
import com.yudistudios.foodland.utils.Result
import com.yudistudios.foodland.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConfirmViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val basketRepository: BasketRepository
) : ViewModel() {

    val foodsInBasket get() = foodRepository.foodsInBasket

    var basket = basketRepository.getBasket().asLiveData()

    val confirmButtonIsClicked = MutableLiveData(false)

    private val hasErrors = MutableLiveData(false)

    val confirmationStatus = MutableLiveData<Status>()

    fun confirmButtonOnClick() {
        confirmButtonIsClicked.value = true
    }

    private suspend fun addFoodsToBasket() {
        val foods = DatabaseUtils.getInstance().foodsInBasket.value?.toList()

        foods?.forEach {
            basketRepository.addFoodToBasket(it).collect { response ->
                Timber.e(response.toString())
                if (response.success != 1L) {
                    hasErrors.postValue(true)
                }
            }
        }
    }

    fun changeFoodBasketByGivenAmount(basketFood: BasketFood, amount: Int) {
        val foodTemp = Food(
            id = basketFood.id.toString(),
            name = basketFood.foodName,
            imageName = basketFood.foodImageName,
            price = basketFood.foodPrice.toString(),
            amount = amount,
            isFavorite = false
        )
        if (amount > 0) {
            foodRepository.addFoodToBasket(foodTemp)
        } else {
            foodRepository.removeFoodFromBasket(foodTemp)
        }
    }

    fun updateBasket(getBasketResponse: GetBasketResponse) {

        CoroutineScope(Dispatchers.IO).launch {

            getBasketResponse.foods.forEach {
                basketRepository.removeFoodFromBasket(it.id).collect { response ->
                    Timber.e(response.toString())
                    if (response.success != 1L) {
                        hasErrors.postValue(true)
                    }
                }
            }

            addFoodsToBasket()

            if (hasErrors.value == false) {
                confirmationStatus.postValue(Status(Result.SUCCESS))
            } else {
                confirmationStatus.postValue(Status(Result.NETWORK_ERROR))
            }

        }
    }

    fun refreshBasketWithFirebaseBasket() {
        confirmationStatus.value = Status(Result.WAITING)
        basket = basketRepository.getBasket().asLiveData()
    }
}