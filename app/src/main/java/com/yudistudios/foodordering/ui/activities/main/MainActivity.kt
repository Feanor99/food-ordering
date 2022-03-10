package com.yudistudios.foodordering.ui.activities.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.yudistudios.foodordering.R
import com.yudistudios.foodordering.databinding.ActivityMainBinding
import com.yudistudios.foodordering.firebase.AuthUtils
import com.yudistudios.foodordering.firebase.DatabaseUtils
import com.yudistudios.foodordering.ui.activities.login.LoginActivity
import com.yudistudios.foodordering.utils.fadeInAnimation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        var sShowBottomNavView = MutableLiveData(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DatabaseUtils.getInstance()

        sShowBottomNavView.observe(this) {
            if (it) {
                binding.bottomNavigationView.fadeInAnimation()
            } else {
                binding.bottomNavigationView.visibility = View.GONE
            }
        }

        checkIfSignedIn()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navHostFragment.navController)

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