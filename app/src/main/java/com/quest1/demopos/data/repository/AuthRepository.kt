package com.quest1.demopos.data.repository

import com.auth0.android.jwt.JWT
import com.quest1.demopos.data.network.AuthApiService
import com.quest1.demopos.data.network.LoginRequest
import com.quest1.demopos.data.network.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton

// Data class to hold the result of a successful login
data class LoginResult(val accessToken: String, val role: String)

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService
) {

    suspend fun login(username: String, password: String): Result<LoginResult> {
        return try {
            val response = apiService.login(LoginRequest(username, password))

            if (response.isSuccessful && response.body() != null) {
                val token = response.body()!!.accessToken
                val decodedJWT = JWT(token)
                val role = decodedJWT.getClaim("role").asString()

                if (role != null) {
                    // Return the new LoginResult object on success
                    Result.success(LoginResult(accessToken = token, role = role))
                } else {
                    Result.failure(Exception("Role not found in token"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Login failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}", e))
        }
    }

    suspend fun register(username: String, password: String, role: String): Result<Unit> {
        return try {
            val response = apiService.register(RegisterRequest(username, password, role))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Registration failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}", e))
        }
    }
}