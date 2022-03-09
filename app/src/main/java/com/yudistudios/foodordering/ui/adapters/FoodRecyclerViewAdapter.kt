package com.yudistudios.foodordering.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yudistudios.foodordering.databinding.ItemFoodBinding
import com.yudistudios.foodordering.retrofit.models.Food
import com.yudistudios.foodordering.retrofit.models.FoodBasket
import com.yudistudios.foodordering.utils.fadeInAnimation
import timber.log.Timber

class FoodRecyclerViewAdapter(
    val clickListeners: FoodRecyclerItemClickListeners,
    val isFoodInBasket: (Int) -> FoodBasket?
) :
    ListAdapter<Food, FoodRecyclerViewAdapter.MyViewHolder>(DiffCallback) {
    class MyViewHolder private constructor(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food, clickListeners: FoodRecyclerItemClickListeners, isFoodInBasket: (Int) -> FoodBasket?) {
            binding.cardView.fadeInAnimation()
            binding.food = food

            binding.buttonAdd.setOnClickListener {
                clickListeners.add(food)
            }

            binding.materialButtonDecrease.setOnClickListener {
                clickListeners.decrease(food)
            }

            binding.materialButtonIncrease.setOnClickListener {
                clickListeners.increase(food)
            }

            binding.executePendingBindings()
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
        holder.bind(getItem(position), clickListeners, isFoodInBasket)
    }

    override fun getItemCount(): Int {
        return currentList.count()
    }
}

class FoodRecyclerItemClickListeners(
    val add: (Food) -> Unit,
    val increase: (Food) -> Unit,
    val decrease: (Food) -> Unit
)

private object DiffCallback : DiffUtil.ItemCallback<Food>() {
    override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
        return oldItem == newItem
    }
}