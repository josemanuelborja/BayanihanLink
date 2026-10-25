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
data class UserResponse(
    val id: String,
    val fullName: String,
    val email: String,
    val contactNumber: String,
    val address: String,
    val accountType: String,
    val isVerified: Boolean? = null,
    val role: String? = null,
    val authToken: String? = null
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

// Request models
data class RequestResponse(
    val id: String,
    val userId: String? = null,
    val disasterType: String? = null,
    val category: String? = null,
    val categories: List<String>? = null,
    val itemNeeded: String? = null,
    val quantity: String? = null,
    val unit: String? = null,
    val description: String? = null,
    val personsAffected: Int? = null,
    val location: String? = null,
    val evidenceImage: String? = null,
    val urgency: String? = null,
    val status: String? = null,
    val currentTimelineStep: Int? = null,
    val dateSubmitted: String? = null
)

data class UpdateRequestStatusBody(
    val status: String? = null,
    val urgency: String? = null,
    val currentTimelineStep: Int? = null
)

// Offer models
data class OfferRequest(
    val donorId: String,
    val requestId: String,
    val quantityOffered: String,
    val unit: String? = null,
    val message: String? = null
)

data class OfferResponse(
    val _id: String? = null,
    val id: String? = null,
    val donorId: String? = null,
    val requestId: String? = null,
    val quantityOffered: String? = null,
    val unit: String? = null,
    val message: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

// Notification models
data class NotificationResponse(
    val _id: String? = null,
    val id: String? = null,
    val userId: String? = null,
    val type: String? = null,
    val title: String? = null,
    val message: String? = null,
    val requestId: String? = null,
    val offerId: String? = null,
    val contentType: String? = null,
    val targetId: String? = null,
    val isRead: Boolean? = null,
    val createdAt: String? = null
)