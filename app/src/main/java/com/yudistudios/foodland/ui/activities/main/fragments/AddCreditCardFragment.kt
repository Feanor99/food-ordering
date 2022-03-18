package com.yudistudios.foodland.ui.activities.main.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yudistudios.foodland.R
import com.yudistudios.foodland.databinding.FragmentAddCreditCardBinding
import com.yudistudios.foodland.models.CreditCard
import com.yudistudios.foodland.security.SecurityUtils
import com.yudistudios.foodland.ui.activities.main.MainActivity
import com.yudistudios.foodland.ui.activities.main.viewmodels.AddCreditCardViewModel
import com.yudistudios.foodland.utils.saveCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class AddCreditCardFragment : Fragment() {

    private val viewModel: AddCreditCardViewModel by viewModels()

    private var _binding: FragmentAddCreditCardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCreditCardBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel

        MainActivity.sShowBottomNavView.value = false

        formatCardNumberRealTime()

        observers()

        back()

        clearFocus()

        return binding.root
    }

    private fun clearFocus() {

        binding.constraintLayout.setOnClickListener {
            binding.editTextExpireYear.clearFocus()
            binding.editTextCardNumber.clearFocus()
            binding.editTextHolderName.clearFocus()
            binding.editTextCvv.clearFocus()
            binding.editTextExpireMonth.clearFocus()
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editTextHolderName.windowToken, 0)
        }
    }


    private fun observers() {

        viewModel.buttonSaveIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                val cardNumber = binding.editTextCardNumber.text.toString()
                val holderName = binding.editTextHolderName.text.toString()
                val cvv = binding.editTextCvv.text.toString()
                val expireMonth = binding.editTextExpireMonth.text.toString()
                val expireYear = binding.editTextExpireYear.text.toString()

                var hasErrors = false

                if (cardNumber.isEmpty() || cardNumber.length != 16) {
                    binding.editTextCardNumber.error = getString(R.string.required)
                    hasErrors = true
                }

                if (holderName.isEmpty()) {
                    binding.editTextHolderName.error = getString(R.string.required)
                    hasErrors = true
                }

                if (cvv.isEmpty() || cvv.length != 3) {
                    binding.editTextCvv.error = getString(R.string.required)
                    hasErrors = true
                }

                if (expireMonth.isEmpty() || expireMonth.length != 2) {
                    binding.editTextExpireMonth.error = getString(R.string.required)
                    hasErrors = true
                }

                if (expireYear.isEmpty() || expireYear.length != 2) {
                    binding.editTextExpireYear.error = getString(R.string.required)
                    hasErrors = true
                }

                if (!hasErrors) {
                    val card = CreditCard(
                        bank = "Ziraat Bankası",
                        cardNo = cardNumber,
                        holderName = holderName,
                        CVV = cvv,
                        expireMonth = expireMonth,
                        expireYear = expireYear
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        val securityUtils = SecurityUtils()
                        val encryptedText =
                            securityUtils.encrypt(requireContext(), card.toString().toByteArray())
                        saveCard(requireContext(), encryptedText!!)
                        withContext(Dispatchers.Main) {
                            Snackbar.make(binding.root, "Credit Card Saved", Snackbar.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                    }
                }
                viewModel.buttonSaveIsClicked.value = false
            }
        }
    }

    private fun formatCardNumberRealTime() {

        binding.editTextCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()
                text?.let {
                    val sb = StringBuilder()
                    var i = 0
                    for (c in text) {
                        i++
                        sb.append(c)
                        if (i % 4 == 0 && i != 16)
                            sb.append("-")

                    }
                    binding.textViewCardNumber.text = sb.toString()
                }
            }

        }

        )
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