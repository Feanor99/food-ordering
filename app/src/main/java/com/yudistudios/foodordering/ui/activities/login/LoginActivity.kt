package com.yudistudios.foodordering.ui.activities.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yudistudios.foodordering.R
import com.yudistudios.foodordering.firebase.AuthUtils
import com.yudistudios.foodordering.firebase.DatabaseUtils
import com.yudistudios.foodordering.ui.activities.main.MainActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        checkIfSignedIn()
    }

    private fun checkIfSignedIn(): Boolean {
        if (AuthUtils.user != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
//        else {
//            DatabaseUtils.getInstance()
//            AuthUtils.signOut(this)
//        }
        return true
    }
}