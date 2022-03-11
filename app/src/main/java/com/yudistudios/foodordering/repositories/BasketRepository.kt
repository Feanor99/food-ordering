package com.yudistudios.foodordering.repositories

import com.yudistudios.foodordering.firebase.AuthUtils
import com.yudistudios.foodordering.retrofit.models.BasketFood
import com.yudistudios.foodordering.retrofit.models.BasketResponse
import com.yudistudios.foodordering.retrofit.models.toFoodBasketPost
import com.yudistudios.foodordering.retrofit.services.BasketService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class BasketRepository @Inject constructor(private val basketService: BasketService) {

    fun getBasket(): Flow<List<BasketFood>> {
        return flow {
            try {
                val response = basketService.getBasket(AuthUtils.user!!.uid)
                Timber.e(response.body().toString())

                emit(response.body()?.foods ?: listOf())
            } catch (e: Exception) {
                Timber.e(e.message.toString())
                emit(listOf())
            }
        }
    }

    fun addFoodToBasket(basketFood: BasketFood): Flow<BasketResponse> {
        return flow {
            try {
                val foodBasketPost = basketFood.toFoodBasketPost()

                foodBasketPost.let {
                    val response = basketService.addFoodToBasket(
                        it.foodName,
                        it.foodImageName,
                        it.foodPrice,
                        it.foodAmount,
                        it.userId
                    )

                    Timber.e(response.body().toString())

                    emit(response.body() ?: BasketResponse(-1, "default"))
                }

            } catch (e: Exception) {
                Timber.e(e.message.toString())
                emit(BasketResponse(-2, "default"))
            }
        }
    }

    fun removeFoodFromBasket(basketFoodId: Int): Flow<BasketResponse> {
        return flow {
            try {
                val response =
                    basketService.removeFoodFromBasket(basketFoodId, AuthUtils.user!!.uid)
                Timber.e(response.body().toString())

                emit(response.body() ?: BasketResponse(-1, "default"))

            } catch (e: Exception) {
                Timber.e(e.message.toString())
                emit(BasketResponse(-2, "default"))
            }
        }
    }

}