package com.yudistudios.foodland.ui.activities.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AccountViewModel : ViewModel() {

    val signOutIsClicked = MutableLiveData(false)
    val pastOrdersIsClicked = MutableLiveData(false)
    val addressesIsClicked = MutableLiveData(false)
    val creditCardsIsClicked = MutableLiveData(false)

    fun signOutOnClick() {
        signOutIsClicked.value = true
    }

    fun pastOrdersOnClick() {
        pastOrdersIsClicked.value = true
    }

    fun addressesOnClick() {
        addressesIsClicked.value = true
    }

    fun creditCardsOnClick() {
        creditCardsIsClicked.value = true
    }
}