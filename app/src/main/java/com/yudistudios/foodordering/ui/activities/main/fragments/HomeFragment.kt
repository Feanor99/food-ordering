package com.yudistudios.foodordering.ui.activities.main.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.yudistudios.foodordering.databinding.FragmentHomeBinding
import com.yudistudios.foodordering.retrofit.models.Food
import com.yudistudios.foodordering.ui.activities.main.viewmodels.HomeViewModel
import com.yudistudios.foodordering.ui.adapters.FoodRecyclerItemClickListeners
import com.yudistudios.foodordering.ui.adapters.FoodRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var isRefreshed = false
    private var searchText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAllFoods()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setRecyclerAndListen()

        refreshLayout()

        sortPriceChipGroup()

        observers()

        keyboardListener()

        search()

        return binding.root
    }

    private fun search() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString()
                searchFoods()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun searchFoods() {
        val adapter = _binding?.recyclerView?.adapter as FoodRecyclerViewAdapter?

        adapter?.let {
            if (searchText.isNullOrEmpty()) {
                adapter.submitList(viewModel.foods.value?.toList() ?: listOf())
            } else {
                val foodList = viewModel.foods.value
                foodList?.let {
                    val filteredList = foodList.filter {
                        it.name.lowercase().contains(searchText.toString().lowercase())
                    }.toList()
                    adapter.submitList(filteredList)
                }
            }
        }
    }

    private fun keyboardListener() {
       binding.editTextSearch.setOnEditorActionListener { v, actionId, event ->
           if (actionId == EditorInfo.IME_ACTION_DONE ||
               event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
           ) {
               val imm =
                   requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
               imm.hideSoftInputFromWindow(v.windowToken, 0)
               binding.editTextSearch.clearFocus()
               true
           } else {
               false
           }
       }
    }

    private fun observers() {

        viewModel.foodsInBasket.observe(viewLifecycleOwner) {
            viewModel.foodsInBasketCount.value = it.size
            viewModel.updateFoodsWithBasket(it)
        }

        viewModel.showSortMenuIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                if (binding.linearLayoutFilter.visibility == View.GONE) {
                    binding.linearLayoutFilter.visibility = View.VISIBLE
                } else {
                    binding.linearLayoutFilter.visibility = View.GONE
                }
                viewModel.showSortMenuIsClicked.value = false
            }
        }
    }

    private fun sortPriceChipGroup() {
        viewModel.priceChipCheckListener(binding.chipGroupPrice, binding.recyclerView)
    }

    private fun refreshLayout() {
        binding.refreshLayout.setOnRefreshListener(
            object : SwipeRefreshLayout.OnRefreshListener {
                override fun onRefresh() {
                    viewModel.getAllFoods()
                    isRefreshed = true

                    binding.chipPriceNone.isChecked = true

                    lifecycleScope.launch {
                        withContext(Dispatchers.Default) {
                            delay(3000)
                            if (_binding != null && binding.refreshLayout.isRefreshing) {
                                binding.refreshLayout.isRefreshing = false
                            }
                        }
                    }
                }
            }
        )
    }

    private fun setRecyclerAndListen() {
        val addFoodToBasket = { food: Food ->
            food.amount = 1
            viewModel.addFoodToBasket(food)
        }
        val increaseAmount = { food: Food ->
            food.amount = 1
            viewModel.addFoodToBasket(food)
        }
        val decreaseAmount = { food: Food ->
            food.amount = -1
            viewModel.addFoodToBasket(food)
        }
        val clickListeners =
            FoodRecyclerItemClickListeners(addFoodToBasket, increaseAmount, decreaseAmount)
        val adapter = FoodRecyclerViewAdapter(clickListeners, viewModel.isFoodInBasket())
        binding.adapter = adapter

        viewModel.foods.observe(viewLifecycleOwner) {
            if (searchText.isNullOrEmpty()) {
                adapter.submitList(it)
            } else {
                searchFoods()
            }

            if (isRefreshed) {
                viewModel.foodsInBasket.value?.let { it1 ->
                    viewModel.updateFoodsWithBasket(it1)
                }
                isRefreshed = false
            }

            if (binding.refreshLayout.isRefreshing) {
                binding.refreshLayout.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}