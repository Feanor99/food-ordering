package com.yudistudios.foodordering.ui.activities.login.fragments

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
import com.yudistudios.foodordering.databinding.FragmentSignInBinding
import com.yudistudios.foodordering.firebase.AuthUtils
import com.yudistudios.foodordering.ui.activities.login.viewmodels.SignInViewModel
import com.yudistudios.foodordering.ui.activities.main.MainActivity

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
                viewModel.buttonSignInClicked()
                true
            } else {
                false
            }
        }
    }

    private fun observers() {
        viewModel.isButtonSignInClicked.observe(viewLifecycleOwner) {
            if (it) {
                binding.editTextPassword.clearFocus()

                if (binding.editTextEmail.text.toString().isEmpty()) {
                    binding.editTextEmail.error = "Required"
                }

                if (binding.editTextPassword.text.toString().isEmpty()) {
                    binding.editTextPassword.error = "Required"
                } else {
                    binding.textInputLayoutPassword.error = "E-mail or password is incorrect"
                }


                viewModel.isButtonSignInClicked.value = false
            }
        }

        viewModel.isButtonGoogleSignInClicked.observe(viewLifecycleOwner) {
            if (it) {
                AuthUtils.signInResultIsSuccess.observe(viewLifecycleOwner) { isSuccess ->
                    if (isSuccess) {
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }

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