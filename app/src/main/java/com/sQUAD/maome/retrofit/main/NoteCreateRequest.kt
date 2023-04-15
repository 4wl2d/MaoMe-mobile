package com.sQUAD.maome.retrofit.main

import com.google.android.gms.maps.model.LatLng

data class NoteCreateRequest(
    val title: String,
    val content: String,
    val latLng: LatLng
)