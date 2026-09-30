package com.example.bayanihanlink

// These classes describe the exact JSON shapes our backend expects/returns.
// Retrofit + Gson automatically convert between these Kotlin objects and JSON.

// What we SEND when registering. Matches authRoutes.js's /register.
data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val contactNumber: String,
    val address: String,
    val accountType: String // "AFFECTED_INDIVIDUAL" or "DONOR_VOLUNTEER"
)

// What we SEND when logging in. Matches authRoutes.js's /login.
data class LoginRequest(
    val email: String,
    val password: String
)

// What the backend SENDS BACK after register or login succeeds.
// Notice: no password field here — the backend never sends that back.
data class UserResponse(
    val id: String,
    val fullName: String,
    val email: String,
    val contactNumber: String,
    val address: String,
    val accountType: String
)