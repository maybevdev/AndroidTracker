package com.example.progr3ssapp.network

import com.example.progr3ssapp.network.models.LoginRequest
import com.example.progr3ssapp.network.models.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApi {
    @POST("auth/local/signin")
    suspend fun signIn(@Body body: LoginRequest): Response<LoginResponse>

    @Multipart
    @POST("auth/local/signup")
    suspend fun signUp(
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): Response<LoginResponse>
}