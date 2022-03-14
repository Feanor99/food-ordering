package com.yudistudios.foodordering.ui.activities.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AccountViewModel : ViewModel() {

    val signOutIsClicked = MutableLiveData(false)

    fun signOutOnClick() {
        signOutIsClicked.value = true
    }
}