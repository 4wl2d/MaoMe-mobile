package com.sQUAD.maome.ui.fragment.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sQUAD.maome.R
import com.sQUAD.maome.databinding.MainProfileFragmentBinding
import com.sQUAD.maome.retrofit.MainApi
import com.sQUAD.maome.retrofit.RetrofitCfg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainProfileFragment : Fragment() {

    private lateinit var binding: MainProfileFragmentBinding
    private lateinit var mainApi: MainApi
    private var retrofitCfg = RetrofitCfg()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences("User_token", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", null)

        retrofitCfg.setToken(token)
        mainApi = retrofitCfg.getMainApiWithToken()

        binding.apply {
            ExitFromAccountButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
                val editor = sharedPreferences?.edit()
                editor?.putString("token", null)
                editor?.apply()
            }
            CoroutineScope(Dispatchers.IO).launch {
                val response = token?.let { mainApi.getUserInfo(it) }

                requireActivity().runOnUiThread {
                    if (response != null) {
                        WelcomeTextView.text = "Welcome, ${response.body()?.username}"
                    } else {
                        WelcomeTextView.text = "Welcome, noname :)"
                    }
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                val response = token?.let { mainApi.getAllNotes(it) }

                requireActivity().runOnUiThread {
                    if (response != null) {
                        TotalMemoriesTextView.text = "Total memories: ${response.body()?.size}"
                    } else {
                        TotalMemoriesTextView.text = "Total memories: 0"
                    }
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                val response = token?.let { mainApi.getUserInfo(it) }

                requireActivity().runOnUiThread {
                    if (response != null) {
                        AccountIDTextView.text = "Account ID: ${response.body()?.id}"
                    } else {
                        AccountIDTextView.text = "Account ID: ???wtf???"
                    }
                }
            }
        }
    }
}