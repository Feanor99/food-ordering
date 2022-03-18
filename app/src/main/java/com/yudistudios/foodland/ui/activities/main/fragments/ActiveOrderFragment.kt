package com.yudistudios.foodland.ui.activities.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yudistudios.foodland.R
import com.yudistudios.foodland.databinding.FragmentActiveOrderBinding
import com.yudistudios.foodland.models.Order
import com.yudistudios.foodland.ui.activities.main.MainActivity
import com.yudistudios.foodland.ui.activities.main.viewmodels.ActiveOrderViewModel
import com.yudistudios.foodland.ui.adapters.OrderRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ActiveOrderFragment : Fragment() {

    private lateinit var viewModel: ActiveOrderViewModel

    private var _binding: FragmentActiveOrderBinding? = null
    private val binding get() = _binding!!

    private lateinit var callback: OnMapReadyCallback
    private lateinit var order: Order

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActiveOrderBinding.inflate(inflater, container, false)

        MainActivity.sShowBottomNavView.value = false

        val args: ActiveOrderFragmentArgs by navArgs()
        order = args.order

        setRecyclerView(order)

        setTotalCost(order)

        setDateText(order)

        back()

        return binding.root
    }

    private fun back() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setDateText(order: Order) {
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = order.date

        binding.date = sdf.format(calendar.time)
    }

    private fun setTotalCost(order: Order) {

        val totalCost = order.items.map {
            (it.foodAmount * it.foodPrice)
        }.sum()
        binding.totalCost = totalCost.toString()
    }

    private fun setRecyclerView(order: Order) {
        val adapter = OrderRecyclerViewAdapter(order.items)
        binding.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callback = OnMapReadyCallback { googleMap ->

            val address = LatLng(order.latitude, order.longitude)
            googleMap.addMarker(
                MarkerOptions().position(address).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.motorbike)
                )
                    .title("Marker in address")
            )

            googleMap.setMinZoomPreference(17.0f)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(address))
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

}