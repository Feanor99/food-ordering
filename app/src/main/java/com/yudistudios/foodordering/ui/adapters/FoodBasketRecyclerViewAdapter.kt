package com.yudistudios.foodordering.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yudistudios.foodordering.databinding.ItemFoodBasketBinding
import com.yudistudios.foodordering.models.BasketFood

class FoodBasketRecyclerViewAdapter(
    private val clickListeners: FoodBasketRecyclerItemClickListeners
) : ListAdapter<BasketFood, FoodBasketRecyclerViewAdapter.MyViewHolder>(DiffCallback()) {

    class MyViewHolder private constructor(private val binding: ItemFoodBasketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(basketFood: BasketFood, clickListeners: FoodBasketRecyclerItemClickListeners) {
            binding.foodBasket = basketFood

            binding.materialButtonDecrease.setOnClickListener {
                clickListeners.decrease(basketFood)
            }

            binding.materialButtonIncrease.setOnClickListener {
                clickListeners.increase(basketFood)
            }

        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFoodBasketBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), clickListeners)
    }

    override fun getItemCount(): Int {
        return currentList.count()
    }

    private class DiffCallback : DiffUtil.ItemCallback<BasketFood>() {
        override fun areItemsTheSame(oldItem: BasketFood, newItem: BasketFood): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BasketFood, newItem: BasketFood): Boolean {
            return oldItem == newItem
        }
    }
}

class FoodBasketRecyclerItemClickListeners(
    val increase: (BasketFood) -> Unit,
    val decrease: (BasketFood) -> Unit
)

