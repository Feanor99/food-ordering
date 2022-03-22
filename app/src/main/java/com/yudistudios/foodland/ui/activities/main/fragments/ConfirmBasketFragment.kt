package com.yudistudios.foodland.ui.activities.main.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yudistudios.foodland.R
import com.yudistudios.foodland.databinding.FragmentConfirmBasketBinding
import com.yudistudios.foodland.models.BasketFood
import com.yudistudios.foodland.retrofit.models.GetBasketResponse
import com.yudistudios.foodland.ui.activities.main.MainActivity
import com.yudistudios.foodland.ui.activities.main.MainActivity.Companion.foodsInBasketCount
import com.yudistudios.foodland.ui.activities.main.viewmodels.ConfirmBasketViewModel
import com.yudistudios.foodland.ui.adapters.BasketFoodRecyclerViewAdapter
import com.yudistudios.foodland.ui.adapters.FoodBasketRecyclerItemClickListeners
import com.yudistudios.foodland.utils.Dialogs
import com.yudistudios.foodland.utils.Result
import com.yudistudios.foodland.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import com.yudistudios.foodland.ui.adapters.SwipeToDeleteCallback

import androidx.recyclerview.widget.ItemTouchHelper




@AndroidEntryPoint
class ConfirmBasketFragment : Fragment() {

    private val viewModel: ConfirmBasketViewModel by viewModels()

    private var _binding: FragmentConfirmBasketBinding? = null
    private val binding get() = _binding!!

    lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfirmBasketBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        MainActivity.sShowBottomNavView.value = true

        setRecyclerView()

        observeFoodsInBasket()

        observers()

        observeConfirmationStatus()

        return binding.root
    }

    private fun observeConfirmationStatus() {

        viewModel.confirmationStatus.observe(viewLifecycleOwner) {
            when (it.result) {
                Result.SUCCESS -> {
                    if (dialog.isShowing) {
                        dialog.cancel()
                    }

                    lifecycleScope.launch {
                        val creditCard = viewModel.getCreditCard(requireContext())

                        when {
                            viewModel.addresses.value.isNullOrEmpty() -> {
                                findNavController().navigate(R.id.action_basketFragment_to_addressesFragment)
                                viewModel.confirmationStatus.value = Status(Result.NONE)
                            }
                            creditCard == null -> {
                                findNavController().navigate(R.id.action_basketFragment_to_creditCardsFragment)
                                MainActivity.sShowBottomNavView.value = false

                                viewModel.confirmationStatus.value = Status(Result.NONE)
                            }
                            else -> {
                                findNavController().navigate(R.id.action_basketFragment_to_payFragment)
                                MainActivity.sShowBottomNavView.value = false

                                viewModel.confirmationStatus.value = Status(Result.NONE)
                            }
                        }
                    }

                }
                Result.NETWORK_ERROR -> {
                    if (dialog.isShowing) {
                        dialog.cancel()
                    }
                    dialog = Dialogs().errorDialog(requireContext())
                    dialog.show()
                    viewModel.confirmationStatus.value = Status(Result.NONE)
                }
                Result.WAITING -> {
                    dialog = Dialogs().loadingDialog(requireContext())
                    dialog.show()
                    viewModel.confirmationStatus.value = Status(Result.NONE)
                }
                else -> return@observe
            }
        }
    }

    private fun observers() {

        viewModel.confirmButtonIsClicked.observe(viewLifecycleOwner) {
            if (it) {

                viewModel.refreshBasketWithFirebaseBasket()

                refreshBasketObserver()

                viewModel.confirmButtonIsClicked.value = false

            }
        }
    }

    private fun observeFoodsInBasket() {

        viewModel.foodsInBasket.observe(viewLifecycleOwner) {

            Timber.e("foods in basket changed")
            val adapter = binding.recyclerView.adapter as BasketFoodRecyclerViewAdapter
            val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter, requireContext()))
            itemTouchHelper.attachToRecyclerView(binding.recyclerView)

            //same list sent if given directly and causes fail to refresh ui
            //so mapping and creating new list
            adapter.submitList(it.map { fb ->
                fb.copy()
            })

            foodsInBasketCount.value = it.map {  fb ->
                fb.foodAmount
            }.sum()

            if (it.isEmpty()) {
                binding.animationView.visibility = View.VISIBLE
                binding.textViewBasketEmpty.visibility = View.VISIBLE
                binding.buttonConfirm.visibility = View.INVISIBLE
            } else {
                binding.buttonConfirm.visibility = View.VISIBLE
                binding.animationView.visibility = View.GONE
                binding.textViewBasketEmpty.visibility = View.GONE
            }

        }

    }

    private fun refreshBasketObserver() {
        var observer: Observer<GetBasketResponse>? = null
        observer = Observer<GetBasketResponse> { response ->

            viewModel.updateBasket(response)

            observer?.let { it1 ->
                viewModel.basket.removeObserver(it1)
            }
        }

        viewModel.basket.observe(viewLifecycleOwner, observer)
    }

    private fun setRecyclerView() {
        val adapter = setupAdapter()
        binding.adapter = adapter
    }

    private fun setupAdapter(): BasketFoodRecyclerViewAdapter {
        val increaseButton = { f: BasketFood ->
            viewModel.changeFoodBasketByGivenAmount(f, 1)
        }

        val decreaseButton = { f: BasketFood ->
            viewModel.changeFoodBasketByGivenAmount(f, -1)
        }

        val delete = { f: BasketFood ->
            viewModel.removeItem(f)
        }

        val clickListeners = FoodBasketRecyclerItemClickListeners(increaseButton, decreaseButton, delete)

        return BasketFoodRecyclerViewAdapter(clickListeners)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}