package com.yudistudios.foodordering.repositories

import androidx.lifecycle.LiveData
import com.yudistudios.foodordering.firebase.DatabaseUtils
import com.yudistudios.foodordering.models.BasketFood
import com.yudistudios.foodordering.models.Order
import javax.inject.Inject

class OrderRepository @Inject constructor(){

    val orders: LiveData<MutableList<Order>>
        get() = DatabaseUtils.getInstance().orders

}