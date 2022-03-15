package com.yudistudios.foodland.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yudistudios.foodland.databinding.ItemFoodBinding
import com.yudistudios.foodland.models.Food

class FoodRecyclerViewAdapter(
    private val clickListeners: FoodRecyclerItemClickListeners
) :
    ListAdapter<Food, FoodRecyclerViewAdapter.MyViewHolder>(DiffCallback()) {
    class MyViewHolder private constructor(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food, clickListeners: FoodRecyclerItemClickListeners) {
            binding.food = food

            binding.cardView.setOnClickListener {
                clickListeners.goDetail(food)
            }

            binding.buttonAdd.setOnClickListener {
                clickListeners.add(food)
            }

            binding.materialButtonDecrease.setOnClickListener {
                clickListeners.decrease(food)
            }

            binding.materialButtonIncrease.setOnClickListener {
                clickListeners.increase(food)
            }

            binding.buttonFavorite.setOnClickListener {
                clickListeners.toggleFavorite(food)
            }

        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFoodBinding.inflate(layoutInflater, parent, false)
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

    private class DiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }
    }
}

class FoodRecyclerItemClickListeners(
    val add: (Food) -> Unit,
    val increase: (Food) -> Unit,
    val decrease: (Food) -> Unit,
    val goDetail: (Food) -> Unit,
    val toggleFavorite: (Food) -> Unit,
)
