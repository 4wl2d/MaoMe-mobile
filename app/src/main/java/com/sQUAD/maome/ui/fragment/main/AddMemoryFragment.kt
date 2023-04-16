package com.sQUAD.maome.ui.fragment.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.FirebaseStorage
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
import java.io.File
import java.util.*


class AddMemoryFragment : Fragment() {

    private lateinit var binding: AddMemoryFragmentBinding
    private lateinit var mainApi: MainApi
    private val ourLocation: LocationViewModel by activityViewModels()
    private var imageUri: Uri? = null
    private val storage = FirebaseStorage.getInstance("gs://maome-840e8.appspot.com")
    private var storageRef = storage.reference.child("images/image.jpg")

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!

                imageUri = fileUri
                binding.imgProfileImageView.setImageURI(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_mainFragment_to_AddMemoryFragment)
            }
            val file = imageUri?.path?.let { it1 -> File(it1) }
            storageRef = storage.reference.child("images/${imageUri?.let { generateRandomName(it) }}")
            val uploadTask = storageRef.putFile(Uri.fromFile(file))
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Изображение загрузилось!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        context,
                        "Изображение не получилось загрузить :(",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }


    // Function to generate a random name for the image
    private fun generateRandomName(uri: Uri): String {
        val fileExtension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(context?.contentResolver?.getType(uri))
        val uniqueId = UUID.randomUUID().toString()
        return "$uniqueId.$fileExtension"
    }

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
                activity?.let { it1 ->
                    ImagePicker.with(it1)
                        .compress(1024)         //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(
                            300,
                            300
                        )  //Final image resolution will be less than 1080 x 1080(Optional)
                        .createIntent { intent ->
                            startForProfileImageResult.launch(intent)
                            binding.imgProfileImageView.visibility = View.VISIBLE
                        }
                }
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
                        Toast.makeText(
                            context,
                            "You captured your memory! Gratz!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}