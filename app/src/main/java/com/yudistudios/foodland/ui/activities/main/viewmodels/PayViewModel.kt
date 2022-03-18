package com.yudistudios.foodland.ui.activities.main.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yudistudios.foodland.repositories.BasketRepository
import com.yudistudios.foodland.repositories.FoodRepository
import com.yudistudios.foodland.models.BasketFood
import com.yudistudios.foodland.models.CreditCard
import com.yudistudios.foodland.models.Order
import com.yudistudios.foodland.repositories.AddressRepository
import com.yudistudios.foodland.repositories.CreditCardRepository
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
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PayViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val basketRepository: BasketRepository,
    private val addressRepository: AddressRepository,
    private val creditCardRepository: CreditCardRepository

) : ViewModel() {

    var basket = basketRepository.getBasket().asLiveData()

    val payButtonIsClicked = MutableLiveData(false)

    private val hasErrors = MutableLiveData(false)

    val addresses get() =  addressRepository.addresses

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
                        latitude = 41.075760,
                        longitude = 28.923424)
                    basketRepository.saveOrder(order)
                } else {
                    clearStatus.value = (Status(Result.NETWORK_ERROR))
                    hasErrors.value = false
                }
            }
        }
    }

    suspend fun getCreditCard(context: Context): CreditCard? {
        return withContext(Dispatchers.IO) {
            creditCardRepository.getDefaultCreditCard(context).firstOrNull()
        }
    }
}