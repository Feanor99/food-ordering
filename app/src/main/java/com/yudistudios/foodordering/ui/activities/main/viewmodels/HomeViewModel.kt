package com.yudistudios.foodordering.ui.activities.main.viewmodels

import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.yudistudios.foodordering.R
import com.yudistudios.foodordering.repositories.FoodRepository
import com.yudistudios.foodordering.retrofit.models.Food
import com.yudistudios.foodordering.retrofit.models.FoodBasket
import com.yudistudios.foodordering.retrofit.models.toFood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val foodRepository: FoodRepository) : ViewModel() {

    var foods = MutableLiveData<List<Food>>()

    val foodsInBasket get() = foodRepository.foodsInBasket

    val foodsInBasketCount = MutableLiveData(0)

    val showSortMenuIsClicked = MutableLiveData(false)

    fun getFoods() {
        viewModelScope.launch {
            foodRepository.getAllFoods().collect {
                foods.value = it
            }
            foodsInBasket.value?.let { updateAmounts(it)}

        }
    }

    fun addFoodToBasket(food: Food, amount: Int) {
        val foodTemp = Food(
            id = food.id,
            name = food.name,
            imageName = food.imageName,
            price = food.price,
            amount = amount
        )
        if (amount > 0) {
            foodRepository.addFoodToBasket(foodTemp)
        } else {
            foodRepository.removeFoodFromBasket(foodTemp)
        }
    }

    fun showSortMenuOnClick() {
        showSortMenuIsClicked.value = true
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
                    resetPriceSort()
                    viewModelScope.launch {
                        delay(1000)
                        recyclerView.smoothScrollToPosition(0)
                    }
                }
            }
        }
    }

    private fun sortFoodsASC() {
        foods.value?.let { it ->
            foods.value = it.sortedWith(compareBy {
                it.price.toDouble()
            })
        }
    }

    private fun sortFoodsDESC() {
        foods.value?.let { it ->
            foods.value = it.sortedWith(compareBy {
                it.price.toDouble()
            }).reversed()
        }
    }

    private fun resetPriceSort() {
        getFoods()
    }

    fun updateAmounts(basket: List<FoodBasket>) {
        val currentFoods: MutableList<Food> = foods.value?.toMutableList() ?: mutableListOf()

        if (foodRepository.lastRemovedFoodIdFromBasket != null) {
            val food = currentFoods.find {
                it.id == foodRepository.lastRemovedFoodIdFromBasket.toString()
            }
            val index = currentFoods.indexOf(food)

            if (index != -1) {
                currentFoods[index] = Food(
                    currentFoods[index].id,
                    currentFoods[index].name,
                    currentFoods[index].imageName,
                    currentFoods[index].price,
                    0
                )
            }

        }

        for (i in basket.indices) {
            val food = currentFoods.find {
                it.id.toInt() == basket[i].foodId
            }
            val index = currentFoods.indexOf(food)
            food?.let {
                currentFoods[index] = basket[i].toFood()
            }
        }

        foods.value = currentFoods.toList()
    }

}