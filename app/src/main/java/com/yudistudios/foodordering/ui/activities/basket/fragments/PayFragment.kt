package com.yudistudios.foodordering.ui.activities.basket.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yudistudios.foodordering.R
import com.yudistudios.foodordering.ui.activities.basket.viewmodels.PayViewModel

class PayFragment : Fragment() {

    companion object {
        fun newInstance() = PayFragment()
    }

    private lateinit var viewModel: PayViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pay, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PayViewModel::class.java)
        // TODO: Use the ViewModel
    }

}