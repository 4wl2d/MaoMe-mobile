package com.sQUAD.maome.retrofit.main

import com.google.android.gms.maps.model.LatLng

data class Note(
    val id: Long,
    val owner: Long,
    val title: String,
    val content: String,
    val createdAt: String,
    val latLng: LatLng,
    val photos: List<Photo>
)
