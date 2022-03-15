package com.yudistudios.foodland.repositories

import androidx.lifecycle.LiveData
import com.yudistudios.foodland.firebase.DatabaseUtils
import com.yudistudios.foodland.models.BasketFood
import com.yudistudios.foodland.models.Order
import javax.inject.Inject

class OrderRepository @Inject constructor(){

    val orders: LiveData<MutableList<Order>>
        get() = DatabaseUtils.getInstance().orders

}