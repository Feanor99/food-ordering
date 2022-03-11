package com.yudistudios.foodordering.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yudistudios.foodordering.retrofit.models.BasketFood
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

    val foodsInBasket = MutableLiveData<MutableList<BasketFood>>().apply {
        value = mutableListOf()
    }

    private val basketReference = database.child("users")
        .child(AuthUtils.user!!.uid)
        .child("BasketItems")

    init {
        listenBasket()
    }

    fun addFoodToBasket(basketFood: BasketFood) {
        basketReference.child("id_${basketFood.id}").setValue(basketFood)
    }

    fun removeFoodFromBasket(basketFood: BasketFood) {
        basketReference.child("id_${basketFood.id}").removeValue()
    }

    private fun listenBasket() {

        val basketListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val foods = snapshot.value as Map<*, *>?
                val temp = mutableListOf<BasketFood>()

                foods?.forEach {
                    temp.add(mapToFoodBasket(it.value as Map<*, *>))
                }

                Timber.e("database basket changed")
                foodsInBasket.value = temp
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

        basketReference.addValueEventListener(basketListener)
    }

    private fun mapToFoodBasket(map: Map<*, *>): BasketFood {
        return BasketFood(
            id = (map["id"] as Long).toInt(),
            foodName = map["foodName"] as String,
            foodImageName = map["foodImageName"] as String,
            foodPrice = (map["foodPrice"] as Long).toInt(),
            foodAmount = (map["foodAmount"] as Long).toInt(),
            userId = map["userId"] as String
        )
    }
}