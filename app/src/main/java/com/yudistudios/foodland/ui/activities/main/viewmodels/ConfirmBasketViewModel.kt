package com.yudistudios.foodland.ui.activities.main.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yudistudios.foodland.firebase.DatabaseUtils
import com.yudistudios.foodland.repositories.BasketRepository
import com.yudistudios.foodland.repositories.FoodRepository
import com.yudistudios.foodland.models.BasketFood
import com.yudistudios.foodland.models.CreditCard
import com.yudistudios.foodland.models.Food
import com.yudistudios.foodland.repositories.AddressRepository
import com.yudistudios.foodland.repositories.CreditCardRepository
import com.yudistudios.foodland.retrofit.models.GetBasketResponse
import com.yudistudios.foodland.utils.Result
import com.yudistudios.foodland.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConfirmBasketViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val basketRepository: BasketRepository,
    private val addressRepository: AddressRepository,
    private val creditCardRepository: CreditCardRepository
) : ViewModel() {

    val foodsInBasket get() = foodRepository.foodsInBasket

    var basket = basketRepository.getBasket().asLiveData()

    val confirmButtonIsClicked = MutableLiveData(false)

    val addresses get() = addressRepository.addresses

    private val hasErrors = MutableLiveData(false)

    val confirmationStatus = MutableLiveData<Status>()

    fun confirmButtonOnClick() {
        confirmButtonIsClicked.value = true
    }

    private suspend fun addFoodsToBasket() {
        val foods = DatabaseUtils.instance.foodsInBasket.value

        async {
            foods?.forEach {
                launch {
                    basketRepository.addFoodToBasket(it).collect { response ->
                        Timber.e(response.toString())
                        if (response.success != 1L) {
                            hasErrors.postValue(true)
                        }
                    }
                }
            }
        }.await()
        
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

    fun removeItem(basketFood: BasketFood) {
        val foodTemp = Food(
            id = basketFood.id.toString(),
            name = basketFood.foodName,
            imageName = basketFood.foodImageName,
            price = basketFood.foodPrice.toString(),
            amount = basketFood.foodAmount,
            isFavorite = false
        )
        foodRepository.deleteFoodFromBasket(foodTemp)
    }

    fun updateBasket(getBasketResponse: GetBasketResponse) {

        CoroutineScope(Dispatchers.IO).launch {

            async {
                getBasketResponse.foods.forEach {
                    launch {
                        basketRepository.removeFoodFromBasket(it.id).collect { response ->
                            Timber.e(response.toString())
                            if (response.success != 1L) {
                                hasErrors.postValue(true)
                            }
                        }
                    }
                }
            }.await()
            

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

    suspend fun getCreditCard(context: Context): CreditCard? {
        return withContext(Dispatchers.IO) {
            creditCardRepository.getDefaultCreditCard(context).firstOrNull()
        }
    }
}
