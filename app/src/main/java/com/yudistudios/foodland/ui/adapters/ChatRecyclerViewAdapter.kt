package com.yudistudios.foodland.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yudistudios.foodland.databinding.ItemChatMessageBinding
import com.yudistudios.foodland.firebase.AuthUtils
import com.yudistudios.foodland.models.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

class ChatRecyclerViewAdapter :
    ListAdapter<ChatMessage, ChatRecyclerViewAdapter.MyViewHolder>(DiffCallback()) {
    class MyViewHolder private constructor(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(chatMessage: ChatMessage) {
           if (chatMessage.senderId == AuthUtils.user!!.uid) {
               binding.cardChatReceive.visibility = View.GONE
               binding.cardChatSend.visibility = View.VISIBLE

               binding.textViewMessageSend.text = chatMessage.content

               val calendar = Calendar.getInstance()
               calendar.timeInMillis = chatMessage.date
               val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
               binding.textViewDateSend.text = sdf.format(calendar.time)
           } else {
               binding.cardChatReceive.visibility = View.VISIBLE
               binding.cardChatSend.visibility = View.GONE

               binding.textViewMessage.text = chatMessage.content

               val calendar = Calendar.getInstance()
               calendar.timeInMillis = chatMessage.date
               val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
               binding.textViewDate.text = sdf.format(calendar.time)
           }
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemChatMessageBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return currentList.count()
    }

    private class DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.content == newItem.content
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}