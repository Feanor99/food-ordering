package com.yudistudios.foodland.ui.activities.main.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yudistudios.foodland.R
import com.yudistudios.foodland.databinding.FragmentSupportBinding
import com.yudistudios.foodland.ui.activities.main.MainActivity
import com.yudistudios.foodland.ui.activities.main.viewmodels.SupportViewModel
import com.yudistudios.foodland.ui.adapters.ChatRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SupportFragment : Fragment() {

    private val viewModel: SupportViewModel by viewModels()

    private var _binding: FragmentSupportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSupportBinding.inflate(inflater, container, false)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .visibility = View.GONE

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setRecyclerView()

        observers()

        observeChat()

        keyboardListener()

        back()

        return binding.root
    }

    private fun back() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeChat() {

        viewModel.chatMessages.observe(viewLifecycleOwner) {
            val adapter = binding.recyclerView.adapter as ChatRecyclerViewAdapter?
            adapter?.let { _ ->
                adapter.submitList(it.sortedWith(
                    compareBy {
                        it.date
                    }
                ).reversed())

                lifecycleScope.launch {
                    delay(500)
                    if (_binding != null) {
                        binding.recyclerView.scrollToPosition(0)
                    }
                }
            }
        }
    }

    private fun observers() {

        viewModel.sendButtonIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                val text = binding.editTextChat.text.toString()
                if (text.isNotEmpty()) {
                    viewModel.sendMessage(text)
                    binding.editTextChat.setText("")
                }

                viewModel.sendButtonIsClicked.value = false
            }
        }
    }

    private fun keyboardListener() {
        binding.editTextChat.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                viewModel.sendButtonOnClick()
                binding.editTextChat.setText("")
                true
            } else {
                false
            }
        }
    }

    private fun setRecyclerView() {
        val adapter = ChatRecyclerViewAdapter()
        binding.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .visibility = View.VISIBLE
    }


}