package com.yudistudios.foodordering.ui.activities.basket.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yudistudios.foodordering.R
import com.yudistudios.foodordering.databinding.FragmentPayBinding
import com.yudistudios.foodordering.ui.activities.basket.viewmodels.PayViewModel
import com.yudistudios.foodordering.ui.adapters.OrderRecyclerViewAdapter
import com.yudistudios.foodordering.utils.Dialogs
import com.yudistudios.foodordering.utils.HttpRequestResult
import com.yudistudios.foodordering.utils.HttpRequestStatus
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.math.BigDecimal

@AndroidEntryPoint
class PayFragment : Fragment() {

    private val viewModel: PayViewModel by viewModels()

    private var _binding: FragmentPayBinding? = null
    private val binding get() = _binding!!

    lateinit var dialog: AlertDialog

    private val callback = OnMapReadyCallback { googleMap ->

        val address = LatLng(41.038872, 29.000634)
        googleMap.addMarker(MarkerOptions().position(address).title("Marker in address"))
        googleMap.setMinZoomPreference(15.0f)
        googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(address))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPayBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.totalCost = "0.00"

        setRecyclerView()

        observeBasket()

        observePayButton()

        observeClearStatus()

        back()

        return binding.root
    }

    private fun back() {
        binding.buttonBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun observeClearStatus() {

        viewModel.clearStatus.observe(viewLifecycleOwner) {
            when (it.result) {
                HttpRequestResult.SUCCESS -> {
                    if (::dialog.isInitialized && dialog.isShowing) {
                        dialog.cancel()
                    }
                    dialog = Dialogs().successDialog(requireContext()) {
                        requireActivity().finish()
                    }
                    dialog.show()
                }
                HttpRequestResult.FAILED -> {
                    if (::dialog.isInitialized && dialog.isShowing) {
                        dialog.cancel()
                    }
                    dialog = Dialogs().errorDialog(requireContext())
                    dialog.show()
                }
                HttpRequestResult.WAITING -> {
                    dialog = Dialogs().loadingDialog(requireContext())
                    dialog.show()
                }
            }
        }
    }

    private fun observePayButton() {

        viewModel.payButtonIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                Timber.e("Pay button clicked")

                val response = viewModel.basket.value
                Timber.e(response.toString())

                if (response != null) {
                    if (response.successCode == 1) {
                        Timber.e("clear basket")
                        viewModel.clearBasket(response.foods)
                        viewModel.clearStatus.value = HttpRequestStatus(HttpRequestResult.WAITING)
                    } else {
                        viewModel.clearStatus.value = HttpRequestStatus(HttpRequestResult.FAILED)
                    }
                } else {
                    viewModel.clearStatus.value = HttpRequestStatus(HttpRequestResult.FAILED)
                }

                viewModel.payButtonIsClicked.value = false
            }
        }
    }

    private fun observeBasket() {

        viewModel.basket.observe(viewLifecycleOwner) { response ->

            if (response.successCode != 1) {
                viewModel.clearStatus.value = HttpRequestStatus(HttpRequestResult.FAILED)

            } else if (response.foods.isNotEmpty()) {

                val adapter = OrderRecyclerViewAdapter(response.foods)
                binding.adapter = adapter

                var total = BigDecimal("0.00")
                response.foods.forEach {
                    total = total.plus(BigDecimal((it.foodAmount * it.foodPrice).toString()))
                }

                binding.totalCost = total.toString()
                Timber.e(total.toString())
            }
        }
    }

    private fun setRecyclerView() {
        val adapter = OrderRecyclerViewAdapter(listOf())
        binding.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.cancel()
        }
    }
}