package com.yudistudios.foodland.ui.activities.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.yudistudios.foodland.R
import com.yudistudios.foodland.databinding.ActivityMainBinding
import com.yudistudios.foodland.firebase.MessagingUtils
import com.yudistudios.foodland.utils.fadeInAnimation
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

        sShowBottomNavView.observe(this) {
            if (it) {
                binding.bottomNavigationView.fadeInAnimation()
            } else {
                binding.bottomNavigationView.visibility = View.GONE
            }
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navHostFragment.navController)

        MessagingUtils.generateToken()

    }

}