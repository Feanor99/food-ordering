package com.yudistudios.foodland.ui.activities.main.viewmodels

import androidx.lifecycle.ViewModel
import com.yudistudios.foodland.repositories.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListOrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    val orders get() = orderRepository.orders

}