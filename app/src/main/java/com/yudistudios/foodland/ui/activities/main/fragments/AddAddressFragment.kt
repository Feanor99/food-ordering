package com.yudistudios.foodland.ui.activities.main.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.yudistudios.foodland.R
import com.yudistudios.foodland.databinding.FragmentAddAddressBinding
import com.yudistudios.foodland.models.Address
import com.yudistudios.foodland.ui.activities.main.viewmodels.AddAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AddAddressFragment : Fragment() {

    private val viewModel: AddAddressViewModel by viewModels()

    private var _binding: FragmentAddAddressBinding? = null
    private val binding get() = _binding!!

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLocation: Location = locationResult.lastLocation
            removeLocationObserver()

            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            val callback = OnMapReadyCallback { googleMap ->
                mLatitude = mLocation.latitude
                mLongitude = mLocation.longitude

                val address = LatLng(mLocation.latitude, mLocation.longitude)
                val marker = MarkerOptions().position(address).title("Marker in address")

                googleMap.addMarker(marker)
                googleMap.setMinZoomPreference(15.0f)
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(address))

                googleMap.setOnMapClickListener {
                    mLatitude = it.latitude
                    mLongitude = it.longitude
                    googleMap.clear()
                    googleMap.addMarker(MarkerOptions().position(it).title("Marker in address"))
                }
            }
            mapFragment?.getMapAsync(callback)


            Timber.e("current location: $mLocation")
        }
    }

    fun removeLocationObserver() {
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
    }

    // method to check for permissions
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    }

    // method to check
    // if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                requestNewLocationData()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                requestNewLocationData()
            }
            else -> {
                Toast.makeText(requireContext(), "Location permission required", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddAddressBinding.inflate(inflater, container, false)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        if (!isLocationEnabled() || !checkPermissions()) {
            Toast.makeText(requireContext(), "Location permission required", Toast.LENGTH_SHORT)
                .show()
        }

        binding.viewModel = viewModel
        binding.layoutAddressDetail.viewModel = viewModel

        observers()

        back()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        val callback = OnMapReadyCallback { googleMap ->
            mLatitude = 41.044313
            mLongitude = 29.035711

            val address = LatLng(mLatitude, mLongitude)
            val marker = MarkerOptions().position(address).title("Marker in address")

            googleMap.addMarker(marker)
            googleMap.setMinZoomPreference(15.0f)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(address))

            googleMap.setOnMapClickListener {
                mLatitude = it.latitude
                mLongitude = it.longitude
                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(it).title("Marker in address"))
            }
        }
        mapFragment?.getMapAsync(callback)
    }


    private fun observers() {

        viewModel.buttonUseThisLocationIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                binding.containerLocationMap.visibility = View.GONE
                binding.containerAddressDetail.visibility = View.VISIBLE
                viewModel.buttonUseThisLocationIsClicked.value = false
            }
        }

        viewModel.buttonSaveIsClicked.observe(viewLifecycleOwner) {
            if (it) {
                val title = binding.layoutAddressDetail.editTextAddressTitle.text.toString()
                val detail = binding.layoutAddressDetail.editTextAddressDetail.text.toString()
                val name = binding.layoutAddressDetail.editTextName.text.toString()
                val lastname = binding.layoutAddressDetail.editTextLastname.text.toString()
                val phoneNumber = binding.layoutAddressDetail.editTextPhoneNumber.text.toString()

                var hasErrors = false

                if (title.isEmpty()) {
                    binding.layoutAddressDetail.editTextAddressTitle.error =
                        getString(R.string.required)
                    hasErrors = true
                }

                if (detail.isEmpty()) {
                    binding.layoutAddressDetail.editTextAddressDetail.error =
                        getString(R.string.required)
                    hasErrors = true
                }

                if (name.isEmpty()) {
                    binding.layoutAddressDetail.editTextLastname.error =
                        getString(R.string.required)
                    hasErrors = true
                }

                if (lastname.isEmpty()) {
                    binding.layoutAddressDetail.editTextLastname.error =
                        getString(R.string.required)
                    hasErrors = true
                }

                if (phoneNumber.isEmpty()) {
                    binding.layoutAddressDetail.editTextPhoneNumber.error =
                        getString(R.string.required)
                    hasErrors = true
                }

                if (!hasErrors) {
                    val address = Address(
                        title = title,
                        detail = detail,
                        name = name,
                        lastname = lastname,
                        phoneNumber = phoneNumber,
                        latitude = mLatitude,
                        longitude = mLongitude
                    )

                    viewModel.addAddress(address)
                    Snackbar.make(binding.root, getString(R.string.address_saved), Snackbar.LENGTH_LONG)
                        .show()
                    findNavController().popBackStack()
                }

                viewModel.buttonSaveIsClicked.value = false
            }
        }
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