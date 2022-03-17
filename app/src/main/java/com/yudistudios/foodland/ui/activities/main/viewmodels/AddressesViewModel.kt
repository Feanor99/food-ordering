package com.yudistudios.foodland.ui.activities.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yudistudios.foodland.repositories.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddressesViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    val addresses get() = addressRepository.addresses

    val addAddressButtonIsClicked = MutableLiveData(false)

    fun addAddressButtonOnClick() {
        addAddressButtonIsClicked.value = true
    }
}