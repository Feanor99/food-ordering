package com.yudistudios.foodordering.ui.activities.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.yudistudios.foodordering.R
import com.yudistudios.foodordering.repositories.FoodRepository
import com.yudistudios.foodordering.retrofit.models.Food
import com.yudistudios.foodordering.retrofit.models.FoodBasket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val foodRepository: FoodRepository) : ViewModel() {

    val foods get() = foodRepository.foods
    val foodsInBasket get() = foodRepository.foodsInBasket

    val foodsInBasketCount = MutableLiveData(0)

    val showSortMenuIsClicked = MutableLiveData(false)

    fun addFoodToBasket(food: Food) {
        foodRepository.addFoodToBasket(food)
    }

    fun updateFoodsWithBasket(basket: List<FoodBasket>) {
        foodRepository.updateAmounts(basket)
    }

    fun isFoodInBasket(): (Int) -> FoodBasket? {
        return { id ->
            val foodBasket = foodsInBasket.value?.find {
                it.foodId == id
            }
            foodBasket
        }
    }

    fun showSortMenuOnClick() {
        showSortMenuIsClicked.value = true
    }

    fun getAllFoods() {
        foodRepository.getAllFoods()
    }

    fun priceChipCheckListener(chipGroup: ChipGroup, recyclerView: RecyclerView) {
        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chip_price_lower -> {
                    sortFoodsASC()
                    viewModelScope.launch {
                        delay(1000)
                        recyclerView.smoothScrollToPosition(0)
                    }
                }
                R.id.chip_price_higher -> {
                    sortFoodsDESC()
                    viewModelScope.launch {
                        delay(1000)
                        recyclerView.smoothScrollToPosition(0)
                    }
                }
                R.id.chip_price_none -> {
                    foodRepository.resetPriceSort()
                    foodsInBasket.value?.let { updateFoodsWithBasket(it)}
                    viewModelScope.launch {
                        delay(1000)
                        recyclerView.smoothScrollToPosition(0)
                    }
                }
            }
        }
    }

    private fun sortFoodsASC() {
        foodRepository.sortFoodsASC()
    }

    private fun sortFoodsDESC() {
        foodRepository.sortFoodsDESC()
    }
}