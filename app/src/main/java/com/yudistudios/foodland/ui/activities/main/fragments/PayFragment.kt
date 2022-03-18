package com.yudistudios.foodland.ui.activities.main.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yudistudios.foodland.R
import com.yudistudios.foodland.databinding.FragmentPayBinding
import com.yudistudios.foodland.ui.activities.main.MainActivity
import com.yudistudios.foodland.ui.activities.main.viewmodels.PayViewModel
import com.yudistudios.foodland.ui.adapters.OrderRecyclerViewAdapter
import com.yudistudios.foodland.utils.Dialogs
import com.yudistudios.foodland.utils.Result
import com.yudistudios.foodland.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal

@AndroidEntryPoint
class PayFragment : Fragment() {

    private val viewModel: PayViewModel by viewModels()

    private var _binding: FragmentPayBinding? = null
    private val binding get() = _binding!!

    lateinit var dialog: AlertDialog

    private val callback = OnMapReadyCallback { googleMap ->
        val addresses = viewModel.addresses.value

        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val latLng = LatLng(address.latitude, address.longitude)
            googleMap.addMarker(MarkerOptions().position(latLng).title("Marker in address"))
            googleMap.setMinZoomPreference(19.0f)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            binding.addressTitle = address.title
        }
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
        binding.addressTitle = getString(R.string.select_address)
        binding.last4Digits = "0000"

        setRecyclerView()

        observeBasket()

        observePayButton()

        observeClearStatus()

        back()

        getCreditCard()

        return binding.root
    }

    private fun getCreditCard() {

        lifecycleScope.launch {
            val creditCard = viewModel.getCreditCard(requireContext())
            binding.last4Digits = creditCard?.cardNo?.substring(12)
        }
    }

    private fun back() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
            findNavController().popBackStack()
        }
    }

    private fun observeClearStatus() {

        viewModel.clearStatus.observe(viewLifecycleOwner) {
            when (it.result) {
                Result.SUCCESS -> {
                    if (::dialog.isInitialized && dialog.isShowing) {
                        dialog.cancel()
                    }
                    dialog = Dialogs().successDialog(requireContext()) {
                        if (::dialog.isInitialized && dialog.isShowing) {
                            dialog.dismiss()
                        }
                        findNavController().popBackStack(R.id.basketFragment, inclusive = true)
                    }
                    dialog.show()
                }
                Result.NETWORK_ERROR -> {
                    if (::dialog.isInitialized && dialog.isShowing) {
                        dialog.cancel()
                    }
                    dialog = Dialogs().errorDialog(requireContext())
                    dialog.show()
                }
                Result.WAITING -> {
                    dialog = Dialogs().loadingDialog(requireContext())
                    dialog.show()
                }
                else -> return@observe
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
                        viewModel.clearStatus.value = Status(Result.WAITING)
                    } else {
                        viewModel.clearStatus.value = Status(Result.NETWORK_ERROR)
                    }
                } else {
                    viewModel.clearStatus.value = Status(Result.NETWORK_ERROR)
                }

                viewModel.payButtonIsClicked.value = false
            }
        }
    }

    private fun observeBasket() {

        viewModel.basket.observe(viewLifecycleOwner) { response ->

            if (response.successCode != 1) {
                viewModel.clearStatus.value = Status(Result.NETWORK_ERROR)

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
        MainActivity.sShowBottomNavView.value = true
    }
}