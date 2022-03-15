package com.yudistudios.foodland.ui.activities.login.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {

    var isButtonSignInClicked = MutableLiveData(false)
    var isButtonGoogleSignInClicked = MutableLiveData(false)
    var isCreateAccountClicked = MutableLiveData(false)

    fun buttonSignInOnClick() {
        isButtonSignInClicked.value = true
    }

    fun buttonGoogleSignInOnClick() {
        isButtonGoogleSignInClicked.value = true
    }

    fun createAccountOnClick() {
        isCreateAccountClicked.value = true
    }

}