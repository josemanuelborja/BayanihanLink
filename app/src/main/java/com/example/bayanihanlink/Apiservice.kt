package com.example.bayanihanlink

import retrofit2.http.Body
import retrofit2.http.POST

// This interface describes the "menu" of things we can ask the backend to do.
// Retrofit reads this and automatically writes the actual networking code for us.
interface ApiService {

    // Matches: POST /api/auth/register on the backend
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse

    // Matches: POST /api/auth/login on the backend
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): UserResponse
}