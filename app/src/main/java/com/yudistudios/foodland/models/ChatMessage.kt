package com.yudistudios.foodland.models

data class ChatMessage(
    val senderId: String,
    val content: String,
    val date: Long
)