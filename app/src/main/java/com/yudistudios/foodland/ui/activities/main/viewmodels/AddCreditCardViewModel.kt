package com.yudistudios.foodland.ui.activities.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddCreditCardViewModel : ViewModel() {

    val buttonSaveIsClicked = MutableLiveData(false)

    fun buttonSaveOnClick() {
        buttonSaveIsClicked.value = true
    }
}