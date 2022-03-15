package com.yudistudios.foodland.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yudistudios.foodland.databinding.ItemFoodOrderBinding
import com.yudistudios.foodland.models.BasketFood

class OrderRecyclerViewAdapter(private val mList: List<BasketFood>) :
    RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemFoodOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(basketFood: BasketFood) {
            binding.basketFood = basketFood
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFoodOrderBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}