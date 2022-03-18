package com.yudistudios.foodland.ui.activities.login.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yudistudios.foodland.R
import com.yudistudios.foodland.databinding.FragmentSignUpBinding
import com.yudistudios.foodland.firebase.AuthUtils
import com.yudistudios.foodland.ui.activities.login.viewmodels.SignUpViewModel
import com.yudistudios.foodland.ui.activities.main.MainActivity
import com.yudistudios.foodland.utils.Dialogs
import com.yudistudios.foodland.utils.Result

class SignUpFragment : Fragment() {

    private val viewModel: SignUpViewModel by viewModels()

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInLauncher = AuthUtils.createSignInLauncher(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        observers()

        passwordEditTextActionDone()

        return binding.root
    }

    private fun passwordEditTextActionDone() {
        binding.editTextRePassword.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                viewModel.buttonSignUpOnClick()
                true
            } else {
                false
            }
        }
    }

    private fun observers() {

        AuthUtils.signUpResultIsSuccess.observe(viewLifecycleOwner) { response ->
            when (response.result) {
                Result.SUCCESS -> {
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                Result.NETWORK_ERROR -> {
                    val dialog = Dialogs().errorDialog(requireContext())
                    dialog.show()
                    AuthUtils.resetStatus()
                }
                Result.EMAIL_IN_USE -> {
                    binding.textInputLayoutEmail.error = "Email is already in use"
                }
                else -> return@observe
            }
        }

        viewModel.isButtonSignUpClicked.observe(viewLifecycleOwner) {
            if (it) {
                binding.editTextRePassword.clearFocus()

                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()
                val rePassword = binding.editTextRePassword.text.toString()
                val isPasswordsEqual = password == rePassword

                if (email.isEmpty()) {
                    binding.editTextEmail.error = "Required"
                } else if (!Regex("^(.+@[a-zA-Z0-9]+[.][a-zA-Z0-9]+)$").matches(email)) {
                    binding.textInputLayoutEmail.error = "Email pattern is invalid use @ and ."
                }

                if (password.isEmpty() || password.length < 6) {
                    binding.editTextPassword.error = "Required min 6 characters"
                }

                if (rePassword.isEmpty() || rePassword.length < 6) {
                    binding.editTextRePassword.error = "Required min 6 characters"
                }

                if (!isPasswordsEqual) {
                    binding.textInputLayoutRePassword.error = getString(R.string.password_match_error)
                }

                if (email.isNotEmpty() && password.isNotEmpty() && isPasswordsEqual) {
                    AuthUtils.createAccount(email, password, requireActivity())
                }

                viewModel.isButtonSignUpClicked.value = false
            }
        }

        viewModel.isButtonGoogleSignInClicked.observe(viewLifecycleOwner) {
            if (it) {
                AuthUtils.signIn(signInLauncher)

                viewModel.isButtonGoogleSignInClicked.value = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}