package com.yudistudios.foodland.repositories

import com.yudistudios.foodland.firebase.DatabaseUtils
import com.yudistudios.foodland.models.Address
import javax.inject.Inject

class AddressRepository @Inject constructor() {

    val addresses get() = DatabaseUtils.instance.addresses

    fun addAddress(address: Address) {
        DatabaseUtils.instance.addAddress(address)
    }
}