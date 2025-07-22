package com.quest1.demopos.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Data classes for network requests and responses
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
    val refreshToken: String? = null, // Optional refresh token
    val expiresIn: Long? = null, // Token expiration time
    val user: User? = null // Optional user info
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

// Error response for handling API errors
@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val error: String,
    val message: String,
    val code: Int? = null
)

/**
 * Retrofit interface for the authentication service API.
 *
 * This interface defines the endpoints for user authentication operations
 * including login and registration.
 */
interface AuthApiService {

    /**
     * Authenticates a user with username and password.
     *
     * @param request LoginRequest containing username and password
     * @return Response<LoginResponse> containing access token and user info
     */
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /**
     * Registers a new user account.
     *
     * @param request RegisterRequest containing username, password, and role
     * @return Response<RegisterResponse> containing registration result
     */
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    /**
     * Refreshes the access token using a refresh token.
     *
     * @param refreshToken The refresh token
     * @return Response<LoginResponse> containing new access token
     */
    @POST("refresh")
    suspend fun refreshToken(@Body refreshToken: RefreshTokenRequest): Response<LoginResponse>
}

@JsonClass(generateAdapter = true)
data class RefreshTokenRequest(val refreshToken: String)