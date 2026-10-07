package com.example.bayanihanlink.api

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

// What we SEND when saving changes on the Edit Profile screen.
data class UpdateProfileRequest(
    val fullName: String,
    val email: String,
    val contactNumber: String,
    val address: String
)

// What we SEND on step 1 of Forgot Password (ask for a reset code).
data class SendResetCodeRequest(
    val email: String
)

// What we SEND on step 2 of Forgot Password (check the code).
data class VerifyResetCodeRequest(
    val email: String,
    val code: String
)

// What we SEND on step 3 of Forgot Password (save the new password).
data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String
)

// What we SEND when submitting the Change Password screen.
data class ChangePasswordRequest(
    val userId: String,
    val currentPassword: String,
    val newPassword: String
)

// What the backend sends back after changing a password (just a message, no user data).
data class MessageResponse(
    val message: String
)