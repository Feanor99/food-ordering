package com.yudistudios.foodordering.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yudistudios.foodordering.models.BasketFood
import com.yudistudios.foodordering.models.ChatMessage
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class DatabaseUtils private constructor() {

    companion object {
        private var instance: DatabaseUtils? = null

        fun getInstance(): DatabaseUtils {
            if (instance == null && AuthUtils.user != null) {
                instance = DatabaseUtils()
            }

            return instance!!
        }
    }

    private val database = Firebase.database.reference

    val foodsInBasket = MutableLiveData<MutableList<BasketFood>>().apply {
        value = mutableListOf()
    }

    val orders = MutableLiveData<MutableList<List<BasketFood>>>().apply {
        value = mutableListOf()
    }

    val chatMessages = MutableLiveData<MutableList<ChatMessage>>().apply {
        value = mutableListOf()
    }

    private val basketReference = database.child("users")
        .child(AuthUtils.user!!.uid)
        .child("BasketItems")

    private val orderReference = database.child("users")
        .child(AuthUtils.user!!.uid)
        .child("ActiveOrders")

    private val chatReference = database.child("users")
        .child(AuthUtils.user!!.uid)
        .child("SupportChat")

    init {
        listenBasket()
        listenOrders()
        listenChat()
    }

    fun sendMessage(chatMessage: ChatMessage) {
        chatReference.push().setValue(chatMessage)
    }

    fun addFoodToBasket(basketFood: BasketFood) {
        basketReference.child("id_${basketFood.id}").setValue(basketFood)
    }

    fun removeFoodFromBasket(basketFood: BasketFood) {
        basketReference.child("id_${basketFood.id}").removeValue()
    }

    fun saveOrder(order: List<BasketFood>) {
        orderReference.push().setValue(order)
    }

    fun clearBasket() {
        basketReference.removeValue()
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

    private fun listenOrders() {
        val orderListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.value as Map<*, *>?

                if (data != null) {
                    data.forEach {
                        val order = it.value as ArrayList<*>
                        val ordersTemp = orders.value

                        @Suppress("UNCHECKED_CAST")
                        ordersTemp?.add(order as List<BasketFood>)

                        orders.value = ordersTemp
                    }
                } else {
                    orders.value = mutableListOf()
                }

                Timber.e("order table changed")

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

        orderReference.addValueEventListener(orderListener)
    }

    private fun listenChat() {

        val chatListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.e("chat changed")

                val chat = snapshot.value as Map<*, *>?
                val chatMessagesTemp = mutableListOf<ChatMessage>()

                if (chat != null) {
                    chat.forEach {
                        val data = it.value as Map<*, *>
                        chatMessagesTemp.add(mapToChatMessage(data))
                    }
                } else {
                    val initialMessage = ChatMessage(
                        "support",
                        "Merhaba, nasıl yardımcı olabilirim?",
                        Calendar.getInstance().timeInMillis
                    )
                    sendMessage(initialMessage)
                }

                chatMessages.value = chatMessagesTemp
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

        chatReference.addValueEventListener(chatListener)
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

    private fun mapToChatMessage(map: Map<*, *>): ChatMessage {
        return ChatMessage(
            senderId = map["senderId"] as String,
            content = map["content"] as String,
            date = map["date"] as Long
        )
    }
}