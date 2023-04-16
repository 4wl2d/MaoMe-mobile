package com.sQUAD.maome.service

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.sQUAD.maome.retrofit.MainApi

class MapService : OnMapReadyCallback, OnMapClickListener, OnMapLongClickListener {

    private lateinit var mainApi: MainApi

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setOnMapLongClickListener(this)
    }

    override fun onMapClick(p0: LatLng) {
        //TODO click on map
    }

    override fun onMapLongClick(p0: LatLng) {

    }

}