package com.yudistudios.foodland.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yudistudios.foodland.databinding.ItemBasketFoodBinding
import com.yudistudios.foodland.models.BasketFood

class BasketFoodRecyclerViewAdapter(
    private val clickListeners: FoodBasketRecyclerItemClickListeners
) : ListAdapter<BasketFood, BasketFoodRecyclerViewAdapter.MyViewHolder>(DiffCallback()) {

    class MyViewHolder private constructor(private val binding: ItemBasketFoodBinding) :
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
                val binding = ItemBasketFoodBinding.inflate(layoutInflater, parent, false)
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

    fun deleteItem(basketFood: BasketFood) {
        clickListeners.delete(basketFood)
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
    val decrease: (BasketFood) -> Unit,
    val delete: (BasketFood) -> Unit
)

