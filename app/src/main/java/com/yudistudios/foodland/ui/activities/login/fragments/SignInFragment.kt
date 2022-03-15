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
import androidx.navigation.fragment.findNavController
import com.yudistudios.foodland.R
import com.yudistudios.foodland.databinding.FragmentSignInBinding
import com.yudistudios.foodland.firebase.AuthUtils
import com.yudistudios.foodland.ui.activities.login.viewmodels.SignInViewModel
import com.yudistudios.foodland.ui.activities.main.MainActivity
import com.yudistudios.foodland.utils.Dialogs
import com.yudistudios.foodland.utils.Result

class SignInFragment : Fragment() {

    private val viewModel: SignInViewModel by viewModels()

    private var _binding: FragmentSignInBinding? = null
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
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel

        observers()

        passwordEditTextActionDone()

        return binding.root
    }

    private fun passwordEditTextActionDone() {
        binding.editTextPassword.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                viewModel.buttonSignInOnClick()
                true
            } else {
                false
            }
        }
    }

    private fun observers() {

        AuthUtils.signInResultIsSuccess.observe(viewLifecycleOwner) { response ->
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
                Result.INCORRECT_PASSWORD -> {
                    binding.textInputLayoutPassword.error = getString(R.string.email_password_incorrect)
                    AuthUtils.resetStatus()
                }
                else -> return@observe
            }
        }

        viewModel.isButtonSignInClicked.observe(viewLifecycleOwner) {
            if (it) {
                binding.editTextPassword.clearFocus()

                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()

                if (email.isEmpty()) {
                    binding.editTextEmail.error = "Required"
                }

                if (password.isEmpty()) {
                    binding.editTextPassword.error = "Required"
                }

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    AuthUtils.signIn(email, password, requireActivity())
                }


                viewModel.isButtonSignInClicked.value = false
            }
        }

        viewModel.isButtonGoogleSignInClicked.observe(viewLifecycleOwner) {
            if (it) {
                AuthUtils.signIn(signInLauncher)

                viewModel.isButtonGoogleSignInClicked.value = false
            }
        }

        viewModel.isCreateAccountClicked.observe(viewLifecycleOwner) {
            if (it) {
                val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
                findNavController().navigate(action)

                viewModel.isCreateAccountClicked.value = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}