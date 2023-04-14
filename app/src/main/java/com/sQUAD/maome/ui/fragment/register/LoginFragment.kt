package com.sQUAD.maome.ui.fragment.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sQUAD.maome.R
import com.sQUAD.maome.databinding.FragmentLoginBinding
import com.sQUAD.maome.retrofit.auth.AuthRequest
import com.sQUAD.maome.retrofit.MainApi
import com.sQUAD.maome.viewModels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding // initialize binding for this fragment
    private lateinit var mainApi: MainApi
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем сохраненный токен из SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("User_token", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", null)
        if (token != null) {
            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
        }

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

        binding.apply {
            GoToRegisterButton.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            loginButton.setOnClickListener {
                auth(
                    AuthRequest(
                        usernameLogin.text.toString(),
                        passwordLogin.text.toString()
                    )
                )
            }
        }
    }

    private fun auth(authRequest: AuthRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = mainApi.auth(authRequest)
            val message = response.errorBody()?.string()?.let {
                JSONObject(it).getString("message")
            }
            requireActivity().runOnUiThread {
                binding.errorMessageLogin.text = message
                val user = response.body()
                if (user != null) {
                    viewModel.token.value = user.token

                    // Получаем экземпляр SharedPreferences
                    val sharedPreferences = activity?.getSharedPreferences("User_token", Context.MODE_PRIVATE)

                    // Получаем экземпляр редактора SharedPreferences
                    val editor = sharedPreferences?.edit()

                    // Сохраняем токен в SharedPreferences
                    editor?.putString("token", "")

                    // Применяем изменения
                    editor?.apply()

                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                }
            }
        }
    }
}