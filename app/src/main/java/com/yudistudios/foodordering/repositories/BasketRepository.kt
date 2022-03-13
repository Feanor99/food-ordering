package com.yudistudios.foodordering.repositories

import com.yudistudios.foodordering.firebase.AuthUtils
import com.yudistudios.foodordering.firebase.DatabaseUtils
import com.yudistudios.foodordering.models.BasketFood
import com.yudistudios.foodordering.models.Order
import com.yudistudios.foodordering.models.toFoodBasketPost
import com.yudistudios.foodordering.retrofit.models.BasketResponse
import com.yudistudios.foodordering.retrofit.models.GetBasketResponse
import com.yudistudios.foodordering.retrofit.services.BasketService
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
        DatabaseUtils.getInstance().saveOrder(order)
    }

}