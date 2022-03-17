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
import com.yudistudios.foodland.ui.activities.main.fragments.ActiveOrderFragment
import com.yudistudios.foodland.ui.activities.main.fragments.PayFragment
import com.yudistudios.foodland.utils.fadeInAnimation
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        var sShowBottomNavView = MutableLiveData(true)
        val foodsInBasketCount = MutableLiveData(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sShowBottomNavView.observe(this) {
            if (it) {
                if (binding.bottomNavigationView.visibility == View.GONE)
                    binding.bottomNavigationView.fadeInAnimation()
            } else {
                binding.bottomNavigationView.visibility = View.GONE
            }
        }

        cartItemCount()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment

        NavigationUI.setupWithNavController(
            binding.bottomNavigationView,
            navHostFragment.navController
        )

        MessagingUtils.generateToken()

// get card
//        CoroutineScope(Dispatchers.IO).launch {
//            val s = SecurityUtils()
//            val iv = getIV(this@MainActivity)
//            val cipherText = getCard(this@MainActivity)
//            val text = s.decrypt(cipherText, iv)
//            Timber.e(text.toString())
//        }
// save card
//        CoroutineScope(Dispatchers.IO).launch {
//            val text = "0123456789101112.0523.213.yunus.dilber"
//            val s = SecurityUtils()
//            val ciphertext = s.encrypt(text.toByteArray(), this@MainActivity)
//            saveCard(this@MainActivity, ciphertext!!)
//        }

    }

    private fun cartItemCount() {

        foodsInBasketCount.observe(this) {
            val badge = binding.bottomNavigationView.getOrCreateBadge(R.id.basketFragment)

            if (it == 0) {
                badge.isVisible = false
                badge.clearNumber()
            } else if (it > 0) {
                badge.isVisible = true
                badge.number = it
            }
        }
    }


    override fun onBackPressed() {
        val fragment =
            supportFragmentManager.fragments.last()?.childFragmentManager?.fragments?.get(0)
        if (fragment != null) {
            when (fragment) {
                is PayFragment -> {
                    super.onBackPressed()
                    super.onBackPressed()
                }
                is ActiveOrderFragment -> {
                    super.onBackPressed()
                    super.onBackPressed()
                }
                else -> {
                    super.onBackPressed()
                }
            }
            Timber.e(fragment::class.simpleName)

        } else {
            super.onBackPressed()
        }
    }

}