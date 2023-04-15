package com.sQUAD.maome.ui.fragment.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sQUAD.maome.adapter.NotesRCAdapter
import com.sQUAD.maome.databinding.MainNotesFragmentBinding
import com.sQUAD.maome.retrofit.MainApi
import com.sQUAD.maome.retrofit.main.Note
import com.sQUAD.maome.viewModels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainNotesFragment : Fragment() {

    private lateinit var binding: MainNotesFragmentBinding
    private lateinit var mainAPI: MainApi
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainNotesFragmentBinding.inflate(inflater, container, false)

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
        mainAPI = retrofit.create(MainApi::class.java) // retrofit instance

        // initialize notesList with your data
        val notesAdapter = NotesRCAdapter()
        binding.notesRcView.adapter = notesAdapter
        binding.notesRcView.layoutManager = LinearLayoutManager(requireContext())
        CoroutineScope(Dispatchers.IO).launch {
            val response = token?.let { mainAPI.getAllNotes(it) }

            requireActivity().runOnUiThread {
                binding.apply {
                    if (response != null) {
                        if (response.isSuccessful) {
                            val note = response.body()
                            if (note != null) {
                                notesAdapter.submitList(note)
                                binding.NoNotesTextView.visibility = View.GONE
                            } else {
                                binding.NoNotesTextView.visibility = View.VISIBLE
                            }
                        } else {
                            binding.NoNotesTextView.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        return binding.root
    }
}