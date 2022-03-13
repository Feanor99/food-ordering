package com.yudistudios.foodordering.ui.activities.basket.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yudistudios.foodordering.repositories.BasketRepository
import com.yudistudios.foodordering.repositories.FoodRepository
import com.yudistudios.foodordering.models.BasketFood
import com.yudistudios.foodordering.models.Order
import com.yudistudios.foodordering.utils.Result
import com.yudistudios.foodordering.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PayViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val basketRepository: BasketRepository
) : ViewModel() {

    var basket = basketRepository.getBasket().asLiveData()

    val payButtonIsClicked = MutableLiveData(false)

    private val hasErrors = MutableLiveData(false)

    val clearStatus = MutableLiveData<Status>()

    fun payButtonOnClick() {
        payButtonIsClicked.value = true
    }

    fun clearBasket(foodsForRemove: List<BasketFood>) {

        val orderItems = mutableListOf<BasketFood>()
        orderItems.addAll(foodsForRemove)

        CoroutineScope(Dispatchers.IO).launch {
            foodsForRemove.forEach {
                basketRepository.removeFoodFromBasket(it.id).collect { response ->
                    Timber.e(response.toString())
                    if (response.success != 1L) {
                        hasErrors.postValue(true)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                if (hasErrors.value == false) {
                    clearStatus.value = (Status(Result.SUCCESS))
                    foodRepository.clearAll()

                    val date = Calendar.getInstance().timeInMillis
                    val order = Order(
                        date = date,
                        items = orderItems,
                        longitude = 34.930429,
                        latitude = 32.798766)
                    basketRepository.saveOrder(order)
                } else {
                    clearStatus.value = (Status(Result.NETWORK_ERROR))
                    hasErrors.value = false
                }
            }
        }
    }

}