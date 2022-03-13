package com.yudistudios.foodordering.ui.activities.basket.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.yudistudios.foodordering.R
import com.yudistudios.foodordering.databinding.FragmentConfirmBinding
import com.yudistudios.foodordering.models.BasketFood
import com.yudistudios.foodordering.retrofit.models.GetBasketResponse
import com.yudistudios.foodordering.ui.activities.basket.viewmodels.ConfirmViewModel
import com.yudistudios.foodordering.ui.adapters.FoodBasketRecyclerItemClickListeners
import com.yudistudios.foodordering.ui.adapters.BasketFoodRecyclerViewAdapter
import com.yudistudios.foodordering.utils.Dialogs
import com.yudistudios.foodordering.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ConfirmFragment : Fragment() {

    private val viewModel: ConfirmViewModel by viewModels()

    private var _binding: FragmentConfirmBinding? = null
    private val binding get() = _binding!!

    lateinit var dialog: AlertDialog

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

        observeConfirmationStatus()

        return binding.root
    }

    private fun observeConfirmationStatus() {

        viewModel.confirmationStatus.observe(viewLifecycleOwner) {
            when (it.result) {
                Result.SUCCESS -> {
                    findNavController().navigate(R.id.action_confirmFragment_to_payFragment)
                    if (dialog.isShowing) {
                        dialog.cancel()
                    }
                }
                Result.NETWORK_ERROR -> {
                    if (dialog.isShowing) {
                        dialog.cancel()
                    }
                    dialog = Dialogs().errorDialog(requireContext())
                    dialog.show()
                }
                Result.WAITING -> {
                    dialog = Dialogs().loadingDialog(requireContext())
                    dialog.show()
                }
                else -> return@observe
            }
        }
    }

    private fun back() {
        binding.buttonBack.setOnClickListener {
            requireActivity().finish()
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

            //same list sent if given directly and causes fail to refresh ui
            //so mapping and creating new list
            adapter.submitList(it.map { fb ->
                fb.copy()
            })

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

    private fun setupAdapter(): BasketFoodRecyclerViewAdapter {
        val increaseButton = { f: BasketFood ->
            viewModel.changeFoodBasketByGivenAmount(f, 1)
        }

        val decreaseButton = { f: BasketFood ->
            viewModel.changeFoodBasketByGivenAmount(f, -1)
        }

        val clickListeners = FoodBasketRecyclerItemClickListeners(increaseButton, decreaseButton)

        return BasketFoodRecyclerViewAdapter(clickListeners)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}