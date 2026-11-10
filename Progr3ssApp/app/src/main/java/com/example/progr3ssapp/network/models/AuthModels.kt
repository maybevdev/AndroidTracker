package com.example.progr3ssapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// Sent to POST /auth/local/signin
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

// Returned by both signin and signup
@Serializable
data class LoginResponse(
    val message: String? = null,
    val user: UserDto? = null,
    val tokens: TokensDto? = null
) {
    val accessToken: String? get() = tokens?.accessToken
    val refreshToken: String? get() = tokens?.refreshToken
}

@Serializable
data class TokensDto(
    val accessToken: String? = null,
    val refreshToken: String? = null
)

@Serializable
data class UserDto(
    val id: Long? = null,
    val email: String? = null,
    @SerialName("auth_provider") val authProvider: String? = null,
    val profile: ProfileDto? = null
)

@Serializable
data class ProfileDto(
    val id: Long? = null,
    val email: String? = null,
    val username: String? = null,
    val description: String? = null,
    val profileImageUrl: String? = null,
    val profileImageBase64: String? = null,
    val coverImageUrl: String? = null,
    val fcmToken: String? = null,
    val preferences: JsonObject? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

// The backend's error body: {"statusCode": 403, "message": "Access denied"}
@Serializable
data class ApiError(
    val statusCode: Int? = null,
    val message: String? = null,
    val error: String? = null
)
