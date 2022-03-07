package com.yudistudios.foodordering.firebase

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.yudistudios.foodordering.R

object AuthUtils {

    var user = FirebaseAuth.getInstance().currentUser

    private val mSignInResultIsSuccess = MutableLiveData(false)
    val signInResultIsSuccess: LiveData<Boolean> get() = mSignInResultIsSuccess

    fun createSignInLauncher(fragment: Fragment): ActivityResultLauncher<Intent> {
        return fragment.registerForActivityResult(
                FirebaseAuthUIActivityResultContract()
            )
            { result ->
                onSignInResult(result)
            }
    }

    fun signIn(signInLauncher: ActivityResultLauncher<Intent>) {

        val providers: List<IdpConfig> = listOf(
            GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setTheme(R.style.Theme_FoodOrdering)
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            user = FirebaseAuth.getInstance().currentUser
            mSignInResultIsSuccess.value = true
        }
    }

    fun signOut(context: Context) {
        FirebaseAuth.getInstance().signOut();
        AuthUI.getInstance()
            .signOut(context);
    }
}