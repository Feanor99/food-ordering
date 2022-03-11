package com.yudistudios.foodordering.ui.activities.basket.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yudistudios.foodordering.repositories.BasketRepository
import com.yudistudios.foodordering.repositories.FoodRepository
import com.yudistudios.foodordering.retrofit.models.BasketFood
import com.yudistudios.foodordering.utils.HttpRequestResult
import com.yudistudios.foodordering.utils.HttpRequestStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PayViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val basketRepository: BasketRepository
) : ViewModel() {


    var basket = basketRepository.getBasket().asLiveData()

    val payButtonIsClicked = MutableLiveData(false)

    private val hasErrors = MutableLiveData(false)

    val clearStatus = MutableLiveData<HttpRequestStatus>()

    fun payButtonOnClick() {
        payButtonIsClicked.value = true
    }

    fun clearBasket(foodsForRemove: List<BasketFood>) {

        CoroutineScope(Dispatchers.IO).launch {
            foodsForRemove.forEach {
                basketRepository.removeFoodFromBasket(it.id).collect { response ->
                    Timber.e(response.toString())
                    if (response.success != 1L) {
                        hasErrors.postValue(true)
                    }
                }
            }

            if (hasErrors.value == false) {
                clearStatus.postValue(HttpRequestStatus(HttpRequestResult.SUCCESS))
                foodRepository.clearAll()
            } else {
                clearStatus.postValue(HttpRequestStatus(HttpRequestResult.FAILED))
            }
        }
    }

}