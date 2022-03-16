package com.yudistudios.foodland.ui.activities.main.fragments

import android.app.AlertDialog
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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.yudistudios.foodland.databinding.FragmentHomeBinding
import com.yudistudios.foodland.models.Food
import com.yudistudios.foodland.ui.activities.main.MainActivity.Companion.foodsInBasketCount
import com.yudistudios.foodland.ui.activities.main.MainActivity.Companion.sShowBottomNavView
import com.yudistudios.foodland.ui.activities.main.viewmodels.HomeViewModel
import com.yudistudios.foodland.ui.adapters.FoodRecyclerItemClickListeners
import com.yudistudios.foodland.ui.adapters.FoodRecyclerViewAdapter
import com.yudistudios.foodland.utils.Dialogs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var isRefreshed = false
    private var searchText: String? = null

    private val mustRefreshRecyclerView = MutableLiveData<Boolean>()

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getFoods()

        observeBasket()
        observeFoods()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        sShowBottomNavView.value = true

        setRecyclerView()

        refreshLayout()

        sortPriceChipGroup()

        observers()

        keyboardListener()

        search()

        categoryPriceChipGroup()

        mustRefreshRecyclerView.value = true

        return binding.root
    }

    private fun observeBasket() {
        viewModel.foodsInBasket.observe(this) {
            Timber.e("basket changed")

            val count = it.map { bf ->
                bf.foodAmount
            }.sum()
            foodsInBasketCount.value = count

            if (viewModel.foods.value != null) {
                viewModel.foods.value = viewModel.foods.value
            }

        }

    }

    private fun observeFoods() {
        viewModel.foods.observe(this) {
            if (it.isNotEmpty()) {
                Timber.e("foods changed")
                mustRefreshRecyclerView.value = true
            }
        }
    }


    private fun search() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString()
                lifecycleScope.launch {
                    searchFoods()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun searchFoods() {
        val adapter = _binding?.recyclerView?.adapter as FoodRecyclerViewAdapter?

        adapter?.let {
                val amountsSet = viewModel.updateAmounts()

                val filteredList = amountsSet.filter { f ->
                    f.name.lowercase().contains(searchText.toString().lowercase())
                }.toList()

                adapter.submitList(filteredList)

                lifecycleScope.launch {
                    delay(1000)
                    _binding?.let {
                        binding.recyclerView.smoothScrollToPosition(0)
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

        viewModel.orders.observe(viewLifecycleOwner) {
            if (it.size > 0) {
                binding.layoutActiveOrders.visibility = View.VISIBLE
            } else {
                binding.layoutActiveOrders.visibility = View.GONE
            }
        }

        viewModel.getFoodsResponse.observe(viewLifecycleOwner) {
            if (it.successCode != 1) {
                dialog = Dialogs().errorDialog(requireContext())
                dialog.show()
                binding.animationView.visibility = View.VISIBLE
                binding.textViewSwipe.visibility = View.VISIBLE

            } else {
                binding.animationView.visibility = View.GONE
                binding.textViewSwipe.visibility = View.GONE

            }

            if (_binding != null && binding.refreshLayout.isRefreshing) {
                binding.refreshLayout.isRefreshing = false
            }
        }

        mustRefreshRecyclerView.observe(viewLifecycleOwner) {
            if (it) {
                if (searchText.isNullOrEmpty()) {
                    val amountsSet = viewModel.updateAmounts()

                    val adapter = binding.recyclerView.adapter as FoodRecyclerViewAdapter
                    adapter.submitList(amountsSet)

                } else {
                    searchFoods()
                }

                if (isRefreshed) {
                    isRefreshed = false
                }

                if (binding.refreshLayout.isRefreshing) {
                    binding.refreshLayout.isRefreshing = false
                }
                mustRefreshRecyclerView.value = false
            }
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

        viewModel.basketButtonIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.basketButtonIsClicked.value = false
            }
        }

        viewModel.viewOrdersButtonIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToListOrdersFragment())
                viewModel.viewOrdersButtonIsClicked.value = false
            }
        }

        viewModel.showSearchEditText.observe(viewLifecycleOwner) {
            if (it) {
                lifecycleScope.launch {
                    delay(500)
                    binding.editTextSearch.requestFocus()
                    val imm =
                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.editTextSearch, InputMethodManager.SHOW_IMPLICIT)
                }
            } else {
                binding.editTextSearch.setText("")
                binding.editTextSearch.clearFocus()
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)
            }
        }
    }

    private fun setRecyclerView() {
        val adapter = foodRecyclerViewAdapterSetup()
        binding.adapter = adapter
    }

    private fun sortPriceChipGroup() {
        viewModel.priceChipCheckListener(binding.chipGroupPrice, binding.recyclerView)
    }

    private fun categoryPriceChipGroup() {
        viewModel.categoryChipCheckListener(binding.chipGroupCategory, binding.recyclerView)
    }

    private fun refreshLayout() {
        binding.refreshLayout.setOnRefreshListener(
            object : SwipeRefreshLayout.OnRefreshListener {
                override fun onRefresh() {
                    isRefreshed = true
                    viewModel.getFoods()
                    binding.chipPriceNone.isChecked = true

                }
            }
        )
    }

    private fun foodRecyclerViewAdapterSetup(): FoodRecyclerViewAdapter {

        val addFoodToBasket = { food: Food ->
            viewModel.changeFoodBasketByGivenAmount(food, 1)
        }

        val increaseAmount = { food: Food ->
            viewModel.changeFoodBasketByGivenAmount(food, 1)
        }

        val decreaseAmount = { food: Food ->
            viewModel.changeFoodBasketByGivenAmount(food, -1)
        }

        val goDetail = { food: Food ->
            val action = HomeFragmentDirections.actionHomeFragmentToFoodDetailFragment(food)
            findNavController().navigate(action)
            sShowBottomNavView.value = false
        }

        val toggleFavorite = { food: Food ->
            val favorites = viewModel.favoriteFoods.value?.toMutableList()
            if (favorites != null) {
                if (favorites.contains(food.id)) {
                    favorites.remove(food.id)
                } else {
                    favorites.add(food.id)
                }
                viewModel.updateFavorites(favorites)
            }
        }


        val clickListeners =
            FoodRecyclerItemClickListeners(
                addFoodToBasket,
                increaseAmount,
                decreaseAmount,
                goDetail,
                toggleFavorite
            )

        return FoodRecyclerViewAdapter(clickListeners)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.cancel()
        }

        _binding = null
    }

}