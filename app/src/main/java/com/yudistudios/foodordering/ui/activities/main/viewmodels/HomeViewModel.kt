package com.yudistudios.foodordering.ui.activities.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.yudistudios.foodordering.R
import com.yudistudios.foodordering.repositories.FoodRepository
import com.yudistudios.foodordering.retrofit.models.Food
import com.yudistudios.foodordering.retrofit.models.toFood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    val basketButtonIsClicked = MutableLiveData(false)

    var currentSort = 0

    fun getFoods() {
        viewModelScope.launch {
            foodRepository.getAllFoods().collect {
                when (currentSort) {
                    0 -> foods.value = it
                    1 -> foods.value = sortFoodsASC(it)
                    2 -> foods.value = sortFoodsDESC(it)
                }
            }
        }
    }

    fun changeFoodBasketByGivenAmount(food: Food, amount: Int) {
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

    fun basketButtonOnClick() {
        basketButtonIsClicked.value = true
    }


    fun priceChipCheckListener(chipGroup: ChipGroup, recyclerView: RecyclerView) {
        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chip_price_lower -> {
                    currentSort = 1
                    getFoods()
                    viewModelScope.launch {
                        delay(1000)
                        recyclerView.smoothScrollToPosition(0)
                    }
                }
                R.id.chip_price_higher -> {
                    currentSort = 2
                    getFoods()
                    viewModelScope.launch {
                        delay(1000)
                        recyclerView.smoothScrollToPosition(0)
                    }
                }
                R.id.chip_price_none -> {
                    currentSort = 0
                    getFoods()
                    viewModelScope.launch {
                        delay(1000)
                        recyclerView.smoothScrollToPosition(0)
                    }
                }
            }
        }
    }

    private fun sortFoodsASC(foods: List<Food>?): List<Food>? {
        var sortedFoods: List<Food>? = null
        foods?.let { it ->
            sortedFoods = it.sortedWith(compareBy {
                it.price.toDouble()
            })
        }
        return sortedFoods
    }

    private fun sortFoodsDESC(foods: List<Food>?): List<Food>? {
        var sortedFoods: List<Food>? = null
        foods?.let { it ->
            sortedFoods = it.sortedWith(compareBy {
                it.price.toDouble()
            }).reversed()
        }
        return sortedFoods
    }

    fun updateAmounts(): List<Food> {

        val currentFoods: MutableList<Food>? = foods.value?.map {
            it.amount = 0
            it
        }?.toMutableList()

        Timber.e("update Amounts called")

        currentFoods?.let {
            val basket = foodsInBasket.value
            basket?.let {
                for (i in basket.indices) {
                    val food = currentFoods.find {
                        it.id.toInt() == basket[i].id
                    }
                    val index = currentFoods.indexOf(food)
                    food?.let {
                        currentFoods[index] = basket[i].toFood()
                    }
                }
            }
        }

        return currentFoods?.toList() ?: listOf()
    }

}