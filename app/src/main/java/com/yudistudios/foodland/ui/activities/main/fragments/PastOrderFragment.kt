package com.yudistudios.foodland.ui.activities.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yudistudios.foodland.R
import com.yudistudios.foodland.databinding.FragmentPastOrderBinding
import com.yudistudios.foodland.models.BasketFood
import com.yudistudios.foodland.ui.activities.main.MainActivity
import com.yudistudios.foodland.ui.activities.main.viewmodels.PastOrderViewModel
import com.yudistudios.foodland.ui.adapters.PastOrdersRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PastOrderFragment : Fragment() {

    private val viewModel: PastOrderViewModel by viewModels()

    private var _binding: FragmentPastOrderBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPastOrderBinding.inflate(inflater, container, false)

        MainActivity.sShowBottomNavView.value = false

        setRecyclerView()

        observePastOrders()

        back()

        return binding.root
    }

    private fun setRecyclerView() {

        val adapter = PastOrdersRecyclerViewAdapter(listOf(), orderAgainOnClick())
        binding.adapter = adapter
    }

    private fun orderAgainOnClick(): (order: List<BasketFood>) -> Unit {
        return {
            viewModel.orderAgain(it)
            findNavController().popBackStack()
            findNavController().popBackStack()
            findNavController().navigate(R.id.basketFragment)
        }
    }

    private fun observePastOrders() {

        viewModel.pastOrders.observe(viewLifecycleOwner) {
            val adapter = PastOrdersRecyclerViewAdapter(it.sortedWith(
                compareBy { order ->
                    order.date
                }
            ).reversed(), orderAgainOnClick())
            binding.adapter = adapter
        }
    }


    private fun back() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        MainActivity.sShowBottomNavView.value = true

    }

}