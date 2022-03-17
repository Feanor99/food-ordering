package com.yudistudios.foodland.repositories

import com.yudistudios.foodland.firebase.DatabaseUtils
import com.yudistudios.foodland.models.BasketFood
import javax.inject.Inject

class OrderRepository @Inject constructor(){

    val orders get() = DatabaseUtils.instance.orders

    val pastOrders get() = DatabaseUtils.instance.pastOrders

    fun orderAgain(items: List<BasketFood>) {
        DatabaseUtils.instance.orderAgain(items)
    }
}