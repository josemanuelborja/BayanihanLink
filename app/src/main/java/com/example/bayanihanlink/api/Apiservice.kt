package com.example.bayanihanlink.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

// Retrofit reads this and automatically writes the actual networking code for us.
interface ApiService {

    // Matches: POST /api/auth/register on the backend
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse

    // Matches: POST /api/auth/login on the backend
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): UserResponse

    // Matches: PUT /api/users/:id on the backend
    @PUT("api/users/{id}")
    suspend fun updateProfile(
        @Path("id") id: String,
        @Body request: UpdateProfileRequest,
        @Header("x-user-id") userIdHeader: String? = null,
        @Header("Authorization") authHeader: String? = null,
        @Header("x-auth-token") authTokenHeader: String? = null
    ): UserResponse

    // Matches: POST /api/auth/change-password on the backend
    @POST("api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): MessageResponse

    // Forgot Password step 1
    @POST("api/auth/send-reset-code")
    suspend fun sendResetCode(@Body request: SendResetCodeRequest): MessageResponse

    // Forgot Password step 2
    @POST("api/auth/verify-reset-code")
    suspend fun verifyResetCode(@Body request: VerifyResetCodeRequest): MessageResponse

    // Forgot Password step 3
    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): MessageResponse

    // Requests
    @Multipart
    @POST("api/requests")
    suspend fun createRequest(
        @Part("userId") userId: RequestBody,
        @Part("disasterType") disasterType: RequestBody,
        @Part("category") category: RequestBody,
        @Part("categories") categories: RequestBody? = null,
        @Part("itemNeeded") itemNeeded: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("unit") unit: RequestBody,
        @Part("description") description: RequestBody,
        @Part("personsAffected") personsAffected: RequestBody,
        @Part("location") location: RequestBody,
        @Part evidenceImage: MultipartBody.Part? = null,
        @Header("x-user-id") userIdHeader: String? = null,
        @Header("Authorization") authHeader: String? = null,
        @Header("x-auth-token") authTokenHeader: String? = null
    ): RequestResponse

    @GET("api/requests")
    suspend fun getAllRequests(
        @Query("status") status: String? = null,
        @Query("urgency") urgency: String? = null
    ): List<RequestResponse>

    @GET("api/requests/user/{userId}")
    suspend fun getUserRequests(
        @Path("userId") userId: String,
        @Header("x-user-id") userIdHeader: String? = null,
        @Header("Authorization") authHeader: String? = null,
        @Header("x-auth-token") authTokenHeader: String? = null
    ): List<RequestResponse>

    @GET("api/requests/{id}")
    suspend fun getRequestById(@Path("id") id: String): RequestResponse

    // Offers
    @POST("api/offers")
    suspend fun createOffer(
        @Body request: OfferRequest,
        @Header("x-user-id") userIdHeader: String? = null,
        @Header("Authorization") authHeader: String? = null,
        @Header("x-auth-token") authTokenHeader: String? = null
    ): OfferResponse

    @GET("api/offers/user/{userId}")
    suspend fun getOffersByUser(
        @Path("userId") userId: String,
        @Header("x-user-id") userIdHeader: String? = null,
        @Header("Authorization") authHeader: String? = null,
        @Header("x-auth-token") authTokenHeader: String? = null
    ): List<OfferResponse>

    // Notifications
    @GET("api/notifications/user/{userId}")
    suspend fun getNotifications(
        @Path("userId") userId: String,
        @Header("x-user-id") userIdHeader: String? = null,
        @Header("Authorization") authHeader: String? = null,
        @Header("x-auth-token") authTokenHeader: String? = null
    ): List<NotificationResponse>

    @PUT("api/notifications/{id}/read")
    suspend fun markNotificationRead(
        @Path("id") id: String,
        @Header("x-user-id") userIdHeader: String? = null,
        @Header("Authorization") authHeader: String? = null,
        @Header("x-auth-token") authTokenHeader: String? = null
    ): NotificationResponse

    @PUT("api/notifications/user/{userId}/read-all")
    suspend fun markAllNotificationsRead(
        @Path("userId") userId: String,
        @Header("x-user-id") userIdHeader: String? = null,
        @Header("Authorization") authHeader: String? = null,
        @Header("x-auth-token") authTokenHeader: String? = null
    ): MessageResponse
}