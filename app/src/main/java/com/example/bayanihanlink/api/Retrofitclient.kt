package com.example.bayanihanlink.api

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.json.JSONObject

object RetrofitClient {
    // 10.0.2.2 is a special address that means "the computer running the emulator".
    // The emulator has its OWN "localhost" that is NOT the same as your PC's
    // localhost, so we can't just use http://localhost:3000/ here.
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
fun readableErrorMessage(error: Exception): String {
    return if (error is HttpException) {
        // The backend sent back an error response, like { "error": "Invalid email or password." }
        try {
            val errorBodyText = error.response()?.errorBody()?.string()
            JSONObject(errorBodyText ?: "").optString("error", "Something went wrong. Please try again.")
        } catch (parsingError: Exception) {
            "Something went wrong. Please try again."
        }
    } else {
        // No response at all — usually means the app couldn't reach the server.
        "Could not reach the server. Make sure the backend is running."
    }
}