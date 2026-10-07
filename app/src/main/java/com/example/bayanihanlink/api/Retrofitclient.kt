package com.example.bayanihanlink.api

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.json.JSONObject

object RetrofitClient {
    // This is the IP address of the computer running the backend.
    //
    // A real phone cannot use "localhost" (that would mean the phone itself),
    // and it cannot use "10.0.2.2" either — that only works on the Android
    // emulator. So we use the computer's Wi-Fi IP address instead.
    //
    // If the app suddenly says "Could not reach the server", the Wi-Fi IP has
    // probably changed. Find the new one by running this in PowerShell:
    //     ipconfig
    // and replace the number below with the "IPv4 Address".
    private const val BASE_URL = "http://192.168.110.254:3000/"

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