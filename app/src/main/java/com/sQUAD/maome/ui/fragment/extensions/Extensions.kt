package com.sQUAD.maome.ui.fragment.extensions

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.isLocationPermissionGranted(p: String): Boolean {
    return ContextCompat.checkSelfPermission(
        activity as AppCompatActivity, p) == PackageManager.PERMISSION_GRANTED
}