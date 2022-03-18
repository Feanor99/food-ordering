package com.yudistudios.foodland.ui.activities.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yudistudios.foodland.R
import com.yudistudios.foodland.databinding.FragmentAccountBinding
import com.yudistudios.foodland.firebase.AuthUtils
import com.yudistudios.foodland.ui.activities.login.LoginActivity
import com.yudistudios.foodland.ui.activities.main.MainActivity
import com.yudistudios.foodland.ui.activities.main.viewmodels.AccountViewModel

class AccountFragment : Fragment() {

    private val viewModel: AccountViewModel by viewModels()

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        MainActivity.sShowBottomNavView.value = true

        binding.imageUrl = AuthUtils.user!!.photoUrl?.toString() ?: ""
        binding.email = AuthUtils.user!!.email
        binding.viewModel = viewModel

        observers()

        return binding.root
    }

    private fun observers() {

        viewModel.signOutIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                AuthUtils.signOut(requireContext())

                val intent = Intent(requireActivity(), LoginActivity::class.java)
                requireActivity().startActivity(intent)
                requireActivity().finish()

                viewModel.signOutIsClicked.value = false
            }
        }

        viewModel.pastOrdersIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_accountFragment_to_pastOrderFragment)
                viewModel.pastOrdersIsClicked.value = false
            }
        }

        viewModel.addressesIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_accountFragment_to_addressesFragment)
                viewModel.addressesIsClicked.value = false
            }
        }

        viewModel.creditCardsIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_accountFragment_to_creditCardsFragment)
                viewModel.creditCardsIsClicked.value = false
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}