package com.yudistudios.foodordering.ui.activities.basket.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.yudistudios.foodordering.databinding.FragmentConfirmBinding
import com.yudistudios.foodordering.retrofit.models.BasketFood
import com.yudistudios.foodordering.ui.activities.basket.viewmodels.ConfirmViewModel
import com.yudistudios.foodordering.ui.adapters.FoodBasketRecyclerItemClickListeners
import com.yudistudios.foodordering.ui.adapters.FoodBasketRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ConfirmFragment : Fragment() {

    private val viewModel: ConfirmViewModel by viewModels()

    private var _binding: FragmentConfirmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfirmBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setRecyclerView()

        observeFoodsInBasket()

        observers()

        back()

        return binding.root
    }

    private fun back() {
        binding.buttonBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun observers() {

        viewModel.confirmButtonIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.refreshBasket()

                basketRefreshed()

                viewModel.confirmButtonIsClicked.value = false
            }
        }
    }

    private fun observeFoodsInBasket() {

        viewModel.foodsInBasket.observe(viewLifecycleOwner) {

            Timber.e("foods in basket changed")
            val adapter = binding.recyclerView.adapter as FoodBasketRecyclerViewAdapter

            //same list sent if given directly and causes fail to refresh ui
            //so mapping and creating new list
            adapter.submitList(it.map { fb ->
                fb.copy()
            })

        }

    }

    private fun basketRefreshed() {
        var observer: Observer<List<BasketFood>>? = null
        observer = Observer<List<BasketFood>> { list ->

            viewModel.updateBasket(list)

            observer?.let {
                    it1 -> viewModel.basket.removeObserver(it1)
            }
        }

        viewModel.basket.observe(viewLifecycleOwner, observer)
    }

    private fun setRecyclerView() {
        val adapter = setupAdapter()
        binding.adapter = adapter
    }

    private fun setupAdapter(): FoodBasketRecyclerViewAdapter {
        val increaseButton = { f: BasketFood ->
            viewModel.changeFoodBasketByGivenAmount(f, 1)
        }

        val decreaseButton = { f: BasketFood ->
            viewModel.changeFoodBasketByGivenAmount(f, -1)
        }

        val clickListeners = FoodBasketRecyclerItemClickListeners(increaseButton, decreaseButton)

        return FoodBasketRecyclerViewAdapter(clickListeners)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}