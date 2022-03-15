package com.yudistudios.foodland.ui.activities.login.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {

    var isButtonSignUpClicked = MutableLiveData(false)
    var isButtonGoogleSignInClicked = MutableLiveData(false)

    fun buttonSignUpOnClick() {
        isButtonSignUpClicked.value = true
    }

    fun buttonGoogleSignInOnClick() {
        isButtonGoogleSignInClicked.value = true
    }

}