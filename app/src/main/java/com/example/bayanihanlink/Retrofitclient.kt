package com.example.bayanihanlink

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.json.JSONObject

object RetrofitClient {
    // 10.0.2.2 is a special address that means "the computer running the emulator".
    // The emulator has its OWN "localhost" that is NOT the same as your PC's
    // localhost, so we can't just use http://localhost:3000/ here.
    //
    // NOTE: if you ever test on a REAL phone instead of the emulator, this
    // needs to change to your computer's actual WiFi IP address instead,
    // e.g. "http://192.168.1.5:3000/" — and your phone must be on the same WiFi.
    private const val BASE_URL = "http://10.0.2.2:3000/"

    // "by lazy" means this is only built the FIRST time it's actually used,
    // and reused every time after that (instead of rebuilding it every call).
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// A small helper used whenever a network call fails, so we can show the
// person a readable message instead of a scary technical error.
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