package com.example.bayanihanlink.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Retrofit reads this and automatically writes the actual networking code for us.
interface ApiService {

    // Matches: POST /api/auth/register on the backend
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse

    // Matches: POST /api/auth/login on the backend
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): UserResponse

    // Matches: PUT /api/users/:id on the backend. "{id}" in the URL gets
    // replaced with whatever string is passed into the "id" parameter.
    @PUT("api/users/{id}")
    suspend fun updateProfile(@Path("id") id: String, @Body request: UpdateProfileRequest): UserResponse

    // Matches: POST /api/auth/change-password on the backend
    @POST("api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): MessageResponse
}