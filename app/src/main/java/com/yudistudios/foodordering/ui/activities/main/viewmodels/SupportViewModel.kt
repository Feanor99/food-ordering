package com.yudistudios.foodordering.ui.activities.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yudistudios.foodordering.firebase.AuthUtils
import com.yudistudios.foodordering.models.ChatMessage
import com.yudistudios.foodordering.repositories.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor(private val chatRepository: ChatRepository) : ViewModel() {

    val chatMessages get() = chatRepository.chatMessages

    val sendButtonIsClicked = MutableLiveData(false)

    fun sendButtonOnClick() {
        sendButtonIsClicked.value = true
    }

    fun sendMessage(message: String) {
        val chatMessage = ChatMessage(
            AuthUtils.user!!.uid,
            message,
            Calendar.getInstance().timeInMillis
        )
        chatRepository.sendMessage(chatMessage)
    }
}