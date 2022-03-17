package com.yudistudios.foodland.ui.activities.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yudistudios.foodland.R
import com.yudistudios.foodland.databinding.FragmentAddressesBinding
import com.yudistudios.foodland.ui.activities.main.MainActivity
import com.yudistudios.foodland.ui.activities.main.viewmodels.AddressesViewModel
import com.yudistudios.foodland.ui.adapters.AddressesRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressesFragment : Fragment() {

    private val viewModel: AddressesViewModel by viewModels()

    private var _binding: FragmentAddressesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddressesBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel

        MainActivity.sShowBottomNavView.value = false

        observers()

        setRecyclerView()

        back()

        return binding.root
    }

    private fun setRecyclerView() {
        val adapter = AddressesRecyclerViewAdapter(listOf())
        binding.adapter = adapter
    }

    private fun observers() {

        viewModel.addresses.observe(viewLifecycleOwner) {
            val adapter = AddressesRecyclerViewAdapter(it)
            binding.adapter = adapter
        }

        viewModel.addAddressButtonIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_addressesFragment_to_addAddressFragment)
                viewModel.addAddressButtonIsClicked.value = false
            }
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
    }

}