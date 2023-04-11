package com.sQUAD.maome.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sQUAD.maome.R
import com.sQUAD.maome.databinding.FragmentRegisterBinding
import com.sQUAD.maome.retrofit.MainApi
import com.sQUAD.maome.retrofit.RegisterRequest
import com.sQUAD.maome.viewModels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var mainApi: MainApi
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder() // logcat client(for debugging)
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder() // retrofit creating
            .baseUrl("http://185.209.29.28:8080/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mainApi = retrofit.create(MainApi::class.java) // retrofit instance

        binding.passwordRegister.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                // Вызывается после того, как текст был изменен
                val text = s?.toString()
                Log.d("hehe", "Must be")
                if (text != null && text.length >= 6) {
                    binding.registerButton.visibility = View.VISIBLE
                    binding.errorMessageRegister.text = ""
                } else {
                    binding.registerButton.visibility = View.GONE
                    binding.errorMessageRegister.text = getText(R.string.PasswordAlert)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Вызывается перед тем, как текст будет изменен
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Вызывается в момент изменения текста
            }

        })

        binding.apply {
            backButton.setOnClickListener{
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            registerButton.setOnClickListener {
                register(
                    RegisterRequest(
                        emailRegister.text.toString(),
                        usernameRegister.text.toString(),
                        passwordRegister.text.toString()
                    )
                )
            }
        }
    }

    private fun register(registerRequest: RegisterRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = mainApi.register(registerRequest)
            val errorMessage = response.errorBody()?.string()?.let {
                JSONObject(it).getString("message")
            }
            requireActivity().runOnUiThread {
                binding.errorMessageRegister.text = errorMessage
                val registerResponse = response.body()
                if (registerResponse != null) {
                    binding.successfullyRegisteredTextView.text = registerResponse.message
                    // findNavController().navigate(R.id.action_registerFragment_to_loginFragment)   если убрать комментарий, то он будет кидать на логин, если захотим.
                }
            }
        }
    }
}