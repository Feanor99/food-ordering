package com.yudistudios.foodordering.ui.activities.main.viewmodels

import androidx.lifecycle.ViewModel
import com.yudistudios.foodordering.repositories.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val foodRepository: FoodRepository) : ViewModel() {

    fun getAllFoods() {
        foodRepository.getAllFoods()
    }
}