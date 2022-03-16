package com.yudistudios.foodland.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yudistudios.foodland.databinding.ItemPastOrderBinding
import com.yudistudios.foodland.models.BasketFood
import com.yudistudios.foodland.models.PastOrder
import java.text.SimpleDateFormat
import java.util.*

class PastOrdersRecyclerViewAdapter(
    private val mList: List<PastOrder>,
    private val orderAgainOnClick: (pastOrders: List<BasketFood>) -> Unit
) :
    RecyclerView.Adapter<PastOrdersRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemPastOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: PastOrder, orderAgainOnClick: (pastOrder: List<BasketFood>) -> Unit) {

            binding.buttonOrderAgain.setOnClickListener {
                orderAgainOnClick(order.items)
            }

            val adapter = OrderRecyclerViewAdapter(order.items)
            binding.adapter = adapter

            val date = Calendar.getInstance()
            date.timeInMillis = order.date
            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")

            binding.orderDate = sdf.format(date.time)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPastOrderBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position], orderAgainOnClick)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}