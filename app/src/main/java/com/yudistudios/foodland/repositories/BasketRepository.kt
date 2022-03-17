package com.yudistudios.foodland.repositories

import com.yudistudios.foodland.firebase.AuthUtils
import com.yudistudios.foodland.firebase.DatabaseUtils
import com.yudistudios.foodland.models.BasketFood
import com.yudistudios.foodland.models.Order
import com.yudistudios.foodland.models.toFoodBasketPost
import com.yudistudios.foodland.retrofit.models.BasketResponse
import com.yudistudios.foodland.retrofit.models.GetBasketResponse
import com.yudistudios.foodland.retrofit.services.BasketService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class BasketRepository @Inject constructor(private val basketService: BasketService) {

    fun getBasket(): Flow<GetBasketResponse> {
        return flow {
            try {
                val response = basketService.getBasket(AuthUtils.user!!.uid)
                Timber.e(response.body().toString())

                emit(response.body() ?: GetBasketResponse(listOf(), 0))
            } catch (e: Exception) {
                Timber.e(e.message.toString())
                emit(GetBasketResponse(listOf(), 0))
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

    fun saveOrder(order: Order) {
        DatabaseUtils.instance.saveOrder(order)
    }

}