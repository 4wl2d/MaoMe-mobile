package com.sQUAD.maome.retrofit

import com.sQUAD.maome.retrofit.auth.AuthRequest
import com.sQUAD.maome.retrofit.auth.RegisterRequest
import com.sQUAD.maome.retrofit.auth.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MainApi {

    @POST("auth/signin")
    suspend fun auth(@Body authRequest: AuthRequest): Response<User>

    @POST("auth/signup")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>


}