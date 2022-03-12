package com.yudistudios.foodordering.repositories

import androidx.lifecycle.LiveData
import com.yudistudios.foodordering.firebase.DatabaseUtils
import com.yudistudios.foodordering.models.BasketFood
import javax.inject.Inject

class OrderRepository @Inject constructor(){

    val orders: LiveData<MutableList<List<BasketFood>>>
        get() = DatabaseUtils.getInstance().orders

}