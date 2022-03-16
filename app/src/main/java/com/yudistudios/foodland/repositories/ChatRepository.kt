package com.yudistudios.foodland.repositories

import com.yudistudios.foodland.firebase.DatabaseUtils
import com.yudistudios.foodland.models.ChatMessage
import javax.inject.Inject

class ChatRepository @Inject constructor() {

    val chatMessages get() = DatabaseUtils.getInstance().chatMessages

    fun sendMessage(chatMessage: ChatMessage) {
        DatabaseUtils.getInstance().sendMessage(chatMessage)
    }
}