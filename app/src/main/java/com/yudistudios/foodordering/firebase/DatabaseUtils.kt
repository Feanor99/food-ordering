package com.yudistudios.foodordering.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yudistudios.foodordering.retrofit.models.FoodBasket
import timber.log.Timber

class DatabaseUtils private constructor() {

    companion object {
        private var instance: DatabaseUtils? = null

        fun getInstance(): DatabaseUtils {
            if (instance == null) {
                instance = DatabaseUtils()
            }

            return instance!!
        }
    }


    private val database = Firebase.database.reference

    val foodsInBasket = MutableLiveData<MutableList<FoodBasket>>().apply {
        value = mutableListOf()
    }

    private val basketReference = database.child("users")
        .child(AuthUtils.user!!.uid)
        .child("BasketItems")

    var lastRemovedFoodIdFromBasket: Int? = null

    init {
        listenBasket()
    }

    fun addFoodToBasket(foodBasket: FoodBasket) {
        basketReference.child("${foodBasket.foodId}").setValue(foodBasket)
    }

    fun removeFoodFromBasket(foodBasket: FoodBasket) {
        lastRemovedFoodIdFromBasket = foodBasket.foodId
        basketReference.child("${foodBasket.foodId}").removeValue()
    }

    private fun listenBasket() {

        val basketListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val foods = snapshot.value as ArrayList<*>?
                val temp = mutableListOf<FoodBasket>()

                foods?.forEach {
                    val value = it as Map<*, *>?
                    value?.let {
                        temp.add(mapToFoodBasket(value))
                    }
                }
                foodsInBasket.value = temp
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

        basketReference.addValueEventListener(basketListener)
    }

    private fun mapToFoodBasket(map: Map<*, *>): FoodBasket {
        return FoodBasket(
            foodId = (map["foodId"] as Long).toInt(),
            foodName = map["foodName"] as String,
            foodImageName = map["foodImageName"] as String,
            foodPrice = (map["foodPrice"] as Long).toInt(),
            foodAmount = (map["foodAmount"] as Long).toInt(),
            userId = map["userId"] as String
        )
    }
}