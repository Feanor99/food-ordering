package com.yudistudios.foodordering.ui.activities.login.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {

    var isButtonSignInClicked = MutableLiveData(false)
    var isButtonGoogleSignInClicked = MutableLiveData(false)

    fun buttonSignInClicked() {
        isButtonSignInClicked.value = true
    }

    fun buttonGoogleSignInClicked() {
        isButtonGoogleSignInClicked.value = true
    }

}