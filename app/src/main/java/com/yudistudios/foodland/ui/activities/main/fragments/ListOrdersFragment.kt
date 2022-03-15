package com.yudistudios.foodland.ui.activities.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yudistudios.foodland.databinding.FragmentListOrdersBinding
import com.yudistudios.foodland.models.Order
import com.yudistudios.foodland.ui.activities.main.MainActivity.Companion.sShowBottomNavView
import com.yudistudios.foodland.ui.activities.main.viewmodels.ListOrdersViewModel
import com.yudistudios.foodland.ui.adapters.ListOrdersRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListOrdersFragment : Fragment() {

    private val viewModel: ListOrdersViewModel by viewModels()

    private var _binding: FragmentListOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListOrdersBinding.inflate(inflater, container, false)

        sShowBottomNavView.value = false

        setRecyclerView()

        observeOrders()

        back()

        return binding.root
    }

    private fun back() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setRecyclerView() {
        val adapter = ListOrdersRecyclerViewAdapter(listOf(), navigateOrderDetail())
        binding.adapter = adapter
    }

    private fun navigateOrderDetail(): (order: Order) -> Unit {
        return {
            val action = ListOrdersFragmentDirections.actionListOrdersFragmentToActiveOrderFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun observeOrders() {

        viewModel.orders.observe(viewLifecycleOwner) {
            val adapter = ListOrdersRecyclerViewAdapter(it.sortedWith(
                compareBy { o ->
                    o.date
                }
            ).reversed(), navigateOrderDetail())
            binding.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}