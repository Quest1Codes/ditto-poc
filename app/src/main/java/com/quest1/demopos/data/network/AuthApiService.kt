package com.quest1.demopos.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val username: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val username: String,
    val password: String,
    val role: String
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @field:Json(name = "access_token")
    val accessToken: String,
    val refreshToken: String? = null,
    val expiresIn: Long? = null,

    val user: User? = null
)

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    val message: String,
    val success: Boolean = true
)

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val username: String,
    val role: String,
    val email: String? = null
)

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val error: String,
    val message: String,
    val code: Int? = null
)

interface AuthApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("refresh")
    suspend fun refreshToken(@Body refreshToken: RefreshTokenRequest): Response<LoginResponse>
}

@JsonClass(generateAdapter = true)
data class RefreshTokenRequest(val refreshToken: String)