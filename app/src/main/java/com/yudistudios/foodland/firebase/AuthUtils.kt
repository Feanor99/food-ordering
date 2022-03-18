package com.yudistudios.foodland.firebase

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yudistudios.foodland.R
import com.yudistudios.foodland.utils.Result
import com.yudistudios.foodland.utils.Status
import com.yudistudios.foodland.utils.clearPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

object AuthUtils {

    var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private val mSignInResultIsSuccess = MutableLiveData<Status>()
    val signInResultIsSuccess: LiveData<Status> get() = mSignInResultIsSuccess

    private val mSignUpResultIsSuccess = MutableLiveData<Status>()
    val signUpResultIsSuccess: LiveData<Status> get() = mSignUpResultIsSuccess

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

    fun signIn(email: String, password: String, activity: Activity) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.e("signInUserWithEmail:success")
                    user = Firebase.auth.currentUser
                    mSignInResultIsSuccess.value = Status(Result.SUCCESS)
                } else {
                    // If sign in fails, display a message to the user.
                    Timber.e("signInWithEmail:failure ${task.exception}")

                    when (task.exception) {
                        is com.google.firebase.FirebaseNetworkException -> {
                            mSignInResultIsSuccess.value = Status(Result.NETWORK_ERROR)
                        }
                        else -> {
                            mSignInResultIsSuccess.value = Status(Result.INCORRECT_PASSWORD)
                        }

                    }
                }
            }
    }

    fun createAccount(email: String, password: String, activity: Activity) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.e("createUserWithEmail:success")
                    user = Firebase.auth.currentUser
                    mSignUpResultIsSuccess.value = Status(Result.SUCCESS)
                } else {
                    // If sign in fails, display a message to the user.
                    Timber.e("createUserWithEmail:failure ${task.exception}")

                    when (task.exception) {
                        is com.google.firebase.auth.FirebaseAuthUserCollisionException -> {
                            mSignInResultIsSuccess.value = Status(Result.EMAIL_IN_USE)
                        }
                        is com.google.firebase.FirebaseNetworkException -> {
                            mSignInResultIsSuccess.value = Status(Result.NETWORK_ERROR)
                        }
                    }
                }
            }
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            user = FirebaseAuth.getInstance().currentUser
            mSignInResultIsSuccess.value = Status(Result.SUCCESS)
        } else {
            mSignInResultIsSuccess.value = Status(Result.NETWORK_ERROR)
        }
    }

    fun resetStatus() {
        mSignInResultIsSuccess.value = Status(Result.WAITING)
    }

    fun signOut(context: Context) {
        FirebaseAuth.getInstance().signOut()
        user = null
        DatabaseUtils.destroy()
        mSignInResultIsSuccess.value = Status(Result.NONE)
        mSignUpResultIsSuccess.value = Status(Result.NONE)
        CoroutineScope(Dispatchers.IO).launch {
            clearPreferences(context)
        }
        AuthUI.getInstance()
            .signOut(context)
    }
}