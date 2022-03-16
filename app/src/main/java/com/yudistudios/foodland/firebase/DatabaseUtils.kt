package com.yudistudios.foodland.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yudistudios.foodland.models.BasketFood
import com.yudistudios.foodland.models.ChatMessage
import com.yudistudios.foodland.models.Order
import com.yudistudios.foodland.models.PastOrder
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

    private val mFoodsInBasket = MutableLiveData<List<BasketFood>>().apply { value = listOf() }
    val foodsInBasket: LiveData<List<BasketFood>>
        get() = mFoodsInBasket

    private val mOrders = MutableLiveData<List<Order>>().apply { value = listOf() }
    val orders: LiveData<List<Order>>
        get() = mOrders

    private val mPastOrders = MutableLiveData<List<PastOrder>>().apply { value = listOf() }
    val pastOrders: LiveData<List<PastOrder>>
        get() = mPastOrders


    private val mChatMessages = MutableLiveData<List<ChatMessage>>().apply { value = listOf() }
    val chatMessages: LiveData<List<ChatMessage>>
        get() = mChatMessages

    private val mFavoriteFoods = MutableLiveData<List<String>>().apply { value = listOf() }
    val favoriteFoods: LiveData<List<String>>
        get() = mFavoriteFoods

    private val foodsReference = database.child("users")
        .child(AuthUtils.user!!.uid)
        .child("Foods")

    private val orderReference = database.child("users")
        .child(AuthUtils.user!!.uid)
        .child("ActiveOrders")

    private val chatReference = database.child("users")
        .child(AuthUtils.user!!.uid)
        .child("SupportChat")

    private val pastOrderReference = database.child("users")
        .child(AuthUtils.user!!.uid)
        .child("PastOrders")

    init {
        listenBasket()
        listenOrders()
        listenChat()
        listenPastOrders()
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
        pastOrderReference.child("${Calendar.getInstance().timeInMillis}")
            .setValue(mFoodsInBasket.value)
        foodsReference.child("Basket").removeValue()
    }

    fun orderAgain(orderItems: List<BasketFood>) {
        clearBasket()
        orderItems.forEach {
            addFoodToBasket(it)
        }
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
                        mFavoriteFoods.value = favorites.toList()
                    }
                }

                mFoodsInBasket.value = temp

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

        foodsReference.addValueEventListener(listener)
    }

    private fun listenOrders() {
        val orderListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.e("orders changed")

                val ordersTemp = mutableListOf<Order>()

                (snapshot.value as Map<*, *>?)?.forEach {
                    val date = (it.key as String).toLong()
                    val order = mapToOrder(it.value as Map<*, *>, date)

                    ordersTemp.add(order)
                }

                mOrders.value = ordersTemp

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

                mChatMessages.value = chatMessagesTemp
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

        chatReference.addValueEventListener(chatListener)
    }

    private fun listenPastOrders() {
        val pastOrderListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.e("past orders changed")

                val ordersTemp = mutableListOf<PastOrder>()

                (snapshot.value as Map<*, *>?)?.forEach {
                    val date = (it.key as String).toLong()
                    val order = mapToPastOrder(it.value as ArrayList<*>, date)

                    ordersTemp.add(order)
                }

                mPastOrders.value = ordersTemp

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

        pastOrderReference.addValueEventListener(pastOrderListener)
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

    private fun mapToPastOrder(items: ArrayList<*>, date: Long): PastOrder {

        val listOfItems = mutableListOf<BasketFood>()

        items.forEach {
            if (it is Map<*, *>) {
                listOfItems.add(mapToFoodBasket(it))
            }
        }

        return PastOrder(
            date = date,
            items = listOfItems
        )
    }
}