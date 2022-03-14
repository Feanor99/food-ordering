package com.yudistudios.foodordering.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yudistudios.foodordering.models.BasketFood
import com.yudistudios.foodordering.models.ChatMessage
import com.yudistudios.foodordering.models.Order
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

        fun destroy() {
            instance = null
        }
    }

    private val database = Firebase.database.reference

    val foodsInBasket = MutableLiveData<MutableList<BasketFood>>().apply {
        value = mutableListOf()
    }

    val orders = MutableLiveData<MutableList<Order>>().apply {
        value = mutableListOf()
    }

    val chatMessages = MutableLiveData<MutableList<ChatMessage>>().apply {
        value = mutableListOf()
    }

    val favoriteFoods = MutableLiveData<List<String>>().apply {
        value = listOf()
    }

    private val foodsReference = database.child("users")
        .child(AuthUtils.user!!.uid)
        .child("Foods")

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
        foodsReference.child("Basket").child("id_${basketFood.id}").setValue(basketFood)
    }

    fun removeFoodFromBasket(basketFood: BasketFood) {
        foodsReference.child("Basket").child("id_${basketFood.id}").removeValue()
    }

    fun setFavoriteFoods(ids: List<String>) {
        foodsReference.child("Favorites").setValue(ids)
    }


    fun saveOrder(order: Order) {
        orderReference.child(order.date.toString()).setValue(
            mapOf(
                "items" to order.items,
                "longitude" to order.longitude,
                "latitude" to order.latitude
            )
        )
    }

    fun clearBasket() {
        foodsReference.child("Basket").removeValue()
    }

    private fun listenBasket() {

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.e("database foods data changed")

                val data = snapshot.value as Map<*, *>?
                val temp = mutableListOf<BasketFood>()

                data?.let {
                    val foods = data["Basket"] as Map<*, *>?
                    foods?.forEach {
                        temp.add(mapToFoodBasket(it.value as Map<*, *>))
                    }

                    val favorites = data["Favorites"] as List<String>?
                    favorites?.let {
                        favoriteFoods.value = favorites.toList()
                    }
                }

                foodsInBasket.value = temp

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

        foodsReference.addValueEventListener(listener)
    }

    private fun listenOrders() {
        val orderListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.e("order table changed")

                val ordersTemp = mutableListOf<Order>()

                (snapshot.value as Map<*, *>?)?.forEach {
                    val date = (it.key as String).toLong()
                    val order = mapToOrder(it.value as Map<*, *>, date)

                    ordersTemp.add(order)
                }

                orders.value = ordersTemp

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

    private fun mapToOrder(map: Map<*, *>, date: Long): Order {
        val mapValue = map["items"] as ArrayList<*>
        val listOfItems = mutableListOf<BasketFood>()

        mapValue.forEach {
            if (it is Map<*, *>) {
                listOfItems.add(mapToFoodBasket(it))
            }
        }

        return Order(
            date = date,
            items = listOfItems,
            longitude = map["longitude"] as Double,
            latitude = map["latitude"] as Double
        )
    }
}