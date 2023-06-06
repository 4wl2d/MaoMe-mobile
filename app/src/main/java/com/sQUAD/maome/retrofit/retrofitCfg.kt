package com.sQUAD.maome.retrofit

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitCfg {

    private val BASE_URL = "http://178.20.44.67:8080/api/"
    private lateinit var mainApi: MainApi
    private var token: String? = null
    private val interceptor = HttpLoggingInterceptor()

    fun setToken(tokenSet: String?) {
        Log.d("hehe", "token")
        token = tokenSet
        token?.let { Log.d("hehe", it) }
    }

    private fun initTokenRetrofit(): Retrofit {
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                var requestBuilder = original.newBuilder()
                token?.let { Log.d("hehe", it) }
                if (token != null) {
                    Log.d("hehe", "request")
                    requestBuilder = requestBuilder.header("Authorization", "Bearer $token")
                }
                val request = requestBuilder.method(original.method, original.body).build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder() // retrofit created
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    private fun initRetrofit(): Retrofit {
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder() // logcat client(for debugging)
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder() // retrofit creating
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    fun getMainApiWithoutToken(): MainApi {
        mainApi = initRetrofit().create(MainApi::class.java) // retrofit instance
        Log.d("hehe", "before return")
        return mainApi
    }

    fun getMainApiWithToken(): MainApi {
        mainApi = initTokenRetrofit().create(MainApi::class.java) // retrofit instance
        token?.let { Log.d("hehe", it) }
        return mainApi
    }

}