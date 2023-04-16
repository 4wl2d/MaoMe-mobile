package com.sQUAD.maome.retrofit.main

data class Note(
    val id: Long,
    val owner: Long,
    val title: String,
    val content: String,
    val createdAt: String,
    val latitude: Double,
    val longitude: Double,
    val photos: List<Photo>
)
