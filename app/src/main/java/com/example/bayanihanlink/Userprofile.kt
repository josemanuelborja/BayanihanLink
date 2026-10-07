package com.example.bayanihanlink

// Shared by BOTH the Affected Individual and Donor/Volunteer Profile screens,
// plus the Edit Profile screen — so it lives here in the root package instead
// of inside either one of them.
data class UserProfile(
    val fullName: String,
    val email: String,
    val contactNumber: String,
    val address: String,
    val accountType: String,
    val totalRequests: Int,
    val verifiedRequests: Int,
    val completedRequests: Int
)