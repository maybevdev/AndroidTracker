package com.example.progr3ssapp.network

import com.example.progr3ssapp.network.models.ApiError
import com.example.progr3ssapp.network.models.LoginRequest
import com.example.progr3ssapp.network.models.LoginResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

class AuthRepository(
    private val api: AuthApi = RetrofitClient.authApi
) {
    suspend fun login(email: String, password: String): Result<LoginResponse> = runCatching {
        val response = api.signIn(LoginRequest(email = email, password = password))
        // The backend answers 403 "Access denied" for a wrong email or password
        if (response.code() == 403) error("Wrong email or password")
        response.requireBody("Login failed")
    }

    // Used later for the Register screen
    suspend fun register(
        username: String,
        email: String,
        password: String,
        profileImage: File? = null
    ): Result<LoginResponse> = runCatching {
        val textType = "text/plain".toMediaType()

        val imagePart = profileImage?.let {
            MultipartBody.Part.createFormData(
                "profileImage",
                it.name,
                it.asRequestBody("image/*".toMediaType())
            )
        }

        val response = api.signUp(
            username = username.toRequestBody(textType),
            email = email.toRequestBody(textType),
            password = password.toRequestBody(textType),
            profileImage = imagePart
        )
        response.requireBody("Registration failed")
    }

    private fun Response<LoginResponse>.requireBody(fallback: String): LoginResponse {
        if (isSuccessful) {
            return body() ?: error("Empty response from server")
        }
        error(parseErrorMessage(this, fallback))
    }

    private fun parseErrorMessage(response: Response<*>, fallback: String): String {
        val raw = response.errorBody()?.string()

        if (!raw.isNullOrBlank()) {
            val parsed = try {
                RetrofitClient.json.decodeFromString(ApiError.serializer(), raw)
            } catch (e: Exception) {
                null
            }
            if (parsed?.message != null) {
                return parsed.message
            }
        }

        val message = response.message()
        if (message.isNullOrBlank()) {
            return "$fallback (${response.code()})"
        }
        return message
    }
}