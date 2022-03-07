package com.yudistudios.foodordering.ui.activities.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yudistudios.foodordering.R
import com.yudistudios.foodordering.firebase.AuthUtils
import com.yudistudios.foodordering.ui.activities.login.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkIfSignedIn()
    }

    private fun checkIfSignedIn() {
        if (AuthUtils.user == null) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
//        else {
//            AuthUtils.signOut(this)
//        }
    }
}