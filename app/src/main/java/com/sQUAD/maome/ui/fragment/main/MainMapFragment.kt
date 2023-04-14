package com.sQUAD.maome.ui.fragment.main

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sQUAD.maome.databinding.MainMapFragmentBinding


class MainMapFragment : Fragment() {
    private lateinit var binding: MainMapFragmentBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainMapFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.googleMap
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { map ->
            googleMap = map
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        mapView.onResume()

        // Initialize fusedLocationClient
        val fusedLocationClient =
            activity?.let { LocationServices.getFusedLocationProviderClient(it) }

        // Get user's current location
        fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
            // Use the location object to display the user's current location on the map
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.addMarker(MarkerOptions().position(currentLatLng).title("You are here!"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}