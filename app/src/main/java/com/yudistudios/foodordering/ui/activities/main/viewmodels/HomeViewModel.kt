package com.yudistudios.foodordering.ui.activities.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.yudistudios.foodordering.R
import com.yudistudios.foodordering.models.Food
import com.yudistudios.foodordering.models.clone
import com.yudistudios.foodordering.models.toFood
import com.yudistudios.foodordering.repositories.FoodRepository
import com.yudistudios.foodordering.repositories.OrderRepository
import com.yudistudios.foodordering.retrofit.models.GetFoodsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    var foods = MutableLiveData<List<Food>>()
    var getFoodsResponse = MutableLiveData<GetFoodsResponse>()

    val orders get() = orderRepository.orders

    val foodsInBasket get() = foodRepository.foodsInBasket

    val favoriteFoods get() = foodRepository.favoriteFoods

    val foodsInBasketCount = MutableLiveData(0)

    val showSortMenuIsClicked = MutableLiveData(false)
    val basketButtonIsClicked = MutableLiveData(false)
    val viewOrdersButtonIsClicked = MutableLiveData(false)

    var currentSort = 0

    fun getFoods() {
        viewModelScope.launch {
            foodRepository.getAllFoods().collect {
                getFoodsResponse.value = it

                when (currentSort) {
                    0 -> foods.value = it.foods
                    1 -> foods.value = sortFoodsASC(it.foods)
                    2 -> foods.value = sortFoodsDESC(it.foods)
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
            amount = amount,
            isFavorite = food.isFavorite
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

    fun viewOrdersButtonOnClick() {
        viewOrdersButtonIsClicked.value = true
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
            basket?.let { _ ->
                for (i in basket.indices) {
                    val food = currentFoods.find { f ->
                        f.id.toInt() == basket[i].id
                    }
                    val index = currentFoods.indexOf(food)
                    food?.let {
                        currentFoods[index] = basket[i].toFood()
                    }
                }
            }

            val favorites = favoriteFoods.value
            favorites?.forEach {
                val food = currentFoods.find { f ->
                    f.id == it
                }?.clone()

                val index = currentFoods.indexOf(food)
                food?.isFavorite = true

                food?.let {
                    currentFoods[index] = food
                }
            }
        }

        return currentFoods?.toList() ?: listOf()
    }

    fun updateFavorites(ids: MutableList<String>) {
        foodRepository.updateFavorites(ids.toList())
    }

}