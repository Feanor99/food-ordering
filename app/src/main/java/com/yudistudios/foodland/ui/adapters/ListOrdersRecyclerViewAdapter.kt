package com.yudistudios.foodland.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yudistudios.foodland.databinding.ItemOrderBinding
import com.yudistudios.foodland.models.Order
import java.text.SimpleDateFormat
import java.util.*

class ListOrdersRecyclerViewAdapter(
    private val mList: List<Order>,
    private val cardViewOnClick: (order: Order) -> Unit
) :
    RecyclerView.Adapter<ListOrdersRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order, cardViewOnClick: (order: Order) -> Unit) {

            binding.cardView.setOnClickListener {
                cardViewOnClick(order)
            }
            val date = Calendar.getInstance()
            date.timeInMillis = order.date
            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")

            binding.textViewDate.text = sdf.format(date.time)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemOrderBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position], cardViewOnClick)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}