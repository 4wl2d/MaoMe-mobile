package com.sQUAD.maome.ui.fragment.main

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.sQUAD.maome.R
import com.sQUAD.maome.databinding.AddMemoryFragmentBinding
import com.sQUAD.maome.retrofit.MainApi
import com.sQUAD.maome.retrofit.main.NoteCreateRequest
import com.sQUAD.maome.viewModels.LocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AddMemoryFragment : Fragment() {

    private lateinit var binding: AddMemoryFragmentBinding
    private lateinit var mainApi: MainApi
    private val ourLocation: LocationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddMemoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences("User_token", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", null)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                var requestBuilder = original.newBuilder()
                if (token != null) {
                    requestBuilder = requestBuilder.header("Authorization", "Bearer $token")
                }
                val request = requestBuilder.method(original.method, original.body).build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder() // retrofit created
            .baseUrl("http://185.209.29.28:8080/api/").client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
        mainApi = retrofit.create(MainApi::class.java) // retrofit instance

        // Initialize fusedLocationClient
        val fusedLocationClient =
            activity?.let { LocationServices.getFusedLocationProviderClient(it) }

        // Get user's current location
        fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
            // Use the location object to display the user's current location on the map
            if (location != null) {
                ourLocation.latLng.value = LatLng(location.latitude, location.longitude)
            }
        }

        binding.apply {
            AddMemoryBackButton.setOnClickListener {
                findNavController().navigate(R.id.action_AddMemoryFragment_to_mainFragment)
            }
            PickImageButton.setOnClickListener {

            }
            AddMemoryButton.setOnClickListener {
                val noteCreateRequest = ourLocation.latLng.value?.let { location ->
                    NoteCreateRequest(
                        MemoryNameEditText.text.toString(),
                        MemoryContentEditText.text.toString(),
                        location.latitude,
                        location.longitude
                    )
                }
                CoroutineScope(Dispatchers.IO).launch {
                    token?.let {
                        if (noteCreateRequest != null) {
                            mainApi.createNote(it, noteCreateRequest)
                        }
                    }
                    requireActivity().runOnUiThread {
                        findNavController().navigate(R.id.action_AddMemoryFragment_to_mainFragment)
                        Toast.makeText(context,"You captured your memory! Gratz!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}