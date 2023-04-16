package com.sQUAD.maome.retrofit.main

data class NoteCreateRequest(
    val title: String,
    val content: String,
    val latitude: Double,
    val longitude: Double
)