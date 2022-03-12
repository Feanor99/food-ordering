package com.yudistudios.foodordering.repositories

import androidx.lifecycle.LiveData
import com.yudistudios.foodordering.firebase.DatabaseUtils
import com.yudistudios.foodordering.models.ChatMessage
import javax.inject.Inject

class ChatRepository @Inject constructor() {

    val chatMessages: LiveData<MutableList<ChatMessage>>
        get() = DatabaseUtils.getInstance().chatMessages

    fun sendMessage(chatMessage: ChatMessage) {
        DatabaseUtils.getInstance().sendMessage(chatMessage)
    }
}