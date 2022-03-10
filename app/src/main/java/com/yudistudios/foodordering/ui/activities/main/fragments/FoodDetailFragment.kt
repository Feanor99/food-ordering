package com.yudistudios.foodordering.ui.activities.main.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yudistudios.foodordering.databinding.FragmentFoodDetailBinding
import com.yudistudios.foodordering.ui.activities.main.MainActivity
import com.yudistudios.foodordering.ui.activities.main.viewmodels.FoodDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodDetailFragment : Fragment() {

    private val viewModel: FoodDetailViewModel by viewModels()

    private var _binding: FragmentFoodDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodDetailBinding.inflate(inflater, container, false)

        val args: FoodDetailFragmentArgs by navArgs()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        viewModel.food.value = args.food

        observers()

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        keyboardListener()

        back()

        listenAmountEditText()

        return binding.root
    }

    private fun listenAmountEditText() {
        binding.editTextAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val amount = binding.editTextAmount.text.toString().toIntOrNull()
                amount?.let {
                    if (amount < 0) {
                        binding.editTextAmount.setText("0")
                        viewModel.food.value?.let {
                            val foodTemp = viewModel.food.value!!
                            foodTemp.amount = 0
                            viewModel.food.value = foodTemp
                        }
                    } else {
                        viewModel.food.value?.let {
                            val foodTemp = viewModel.food.value!!
                            foodTemp.amount = amount
                            viewModel.food.value = foodTemp
                        }

                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun back() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun keyboardListener() {
        binding.editTextAmount.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                binding.editTextAmount.clearFocus()
                true
            } else {
                false
            }
        }
    }

    private fun observers() {

        viewModel.increaseButtonIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                val amount = binding.editTextAmount.text.toString().toIntOrNull()
                amount?.let {
                    binding.editTextAmount.setText("${amount + 1}")
                    viewModel.food.value?.let {
                        val foodTemp = viewModel.food.value!!
                        foodTemp.amount = amount + 1
                        viewModel.food.value = foodTemp
                    }
                }
                viewModel.increaseButtonIsClicked.value = false
            }
        }

        viewModel.decreaseButtonIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                val amount = binding.editTextAmount.text.toString().toIntOrNull()
                amount?.let {
                    binding.editTextAmount.setText("${amount - 1}")
                    viewModel.food.value?.let {
                        val foodTemp = viewModel.food.value!!
                        foodTemp.amount = amount - 1
                        viewModel.food.value = foodTemp
                    }
                }
                viewModel.increaseButtonIsClicked.value = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.updateFoodInBasket()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.sShowBottomNavView.value = true
    }

}