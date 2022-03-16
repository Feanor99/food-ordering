package com.yudistudios.foodland.ui.activities.main.viewmodels

import androidx.lifecycle.ViewModel
import com.yudistudios.foodland.models.BasketFood
import com.yudistudios.foodland.repositories.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PastOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    val pastOrders get() = orderRepository.pastOrders

    fun orderAgain(items: List<BasketFood>) {
        orderRepository.orderAgain(items)
    }
}