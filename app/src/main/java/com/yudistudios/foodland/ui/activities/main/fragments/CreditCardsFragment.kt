package com.yudistudios.foodland.ui.activities.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yudistudios.foodland.R
import com.yudistudios.foodland.databinding.FragmentCreditCardsBinding
import com.yudistudios.foodland.ui.activities.main.MainActivity
import com.yudistudios.foodland.ui.activities.main.viewmodels.CreditCardsViewModel
import com.yudistudios.foodland.ui.adapters.CreditCardsRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreditCardsFragment : Fragment() {

    private val viewModel: CreditCardsViewModel by viewModels()

    private var _binding: FragmentCreditCardsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreditCardsBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        val adapter = CreditCardsRecyclerViewAdapter(listOf())
        binding.adapter = adapter

        MainActivity.sShowBottomNavView.value = false

        observers()

        getCreditCard()

        back()

        return binding.root
    }

    private fun getCreditCard() {

        lifecycleScope.launch {
            val card = viewModel.getCreditCard(requireContext())
            card?.let {
                val cards = listOf(card)
                val adapter = CreditCardsRecyclerViewAdapter(cards)
                binding.adapter = adapter
            }
        }
    }

    private fun observers() {

        viewModel.buttonNewCardIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_creditCardsFragment_to_addCreditCardFragment)
                viewModel.buttonNewCardIsClicked.value = false
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