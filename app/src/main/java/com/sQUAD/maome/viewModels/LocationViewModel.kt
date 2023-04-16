package com.sQUAD.maome.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class LocationViewModel : ViewModel() {
    val latLng = MutableLiveData<LatLng>()
}