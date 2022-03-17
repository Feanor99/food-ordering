package com.yudistudios.foodland.ui.activities.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yudistudios.foodland.models.Address
import com.yudistudios.foodland.repositories.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository
    ) : ViewModel() {

    val buttonUseThisLocationIsClicked = MutableLiveData(false)
    val buttonSaveIsClicked = MutableLiveData(false)

    fun buttonUseThisLocationOnClick() {
        buttonUseThisLocationIsClicked.value = true
    }

    fun buttonSaveOnClick() {
        buttonSaveIsClicked.value = true
    }

    fun addAddress(address: Address) {
        addressRepository.addAddress(address)
    }
}