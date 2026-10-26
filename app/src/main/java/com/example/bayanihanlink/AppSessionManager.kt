package com.example.bayanihanlink

import android.content.Context
import com.example.bayanihanlink.api.UserResponse
import org.json.JSONObject

/**
 * Utility functions for consistent Request ID formatting (#BL-00001 format)
 * and persistent session management across app restarts / backgrounding.
 */
object AppSessionManager {
    private const val PREFS_NAME = "bayanihan_session_prefs"
    private const val KEY_USER_JSON = "logged_in_user_json"
    private const val KEY_REQ_COUNTER = "request_id_counter"

    fun saveUserSession(context: Context, user: UserResponse?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (user == null) {
            prefs.edit().remove(KEY_USER_JSON).apply()
        } else {
            val json = JSONObject().apply {
                put("id", user.id)
                put("fullName", user.fullName)
                put("email", user.email)
                put("contactNumber", user.contactNumber)
                put("address", user.address)
                put("accountType", user.accountType)
                put("isVerified", user.isVerified ?: false)
                put("role", user.role ?: "")
                put("authToken", user.authToken ?: "")
            }.toString()
            prefs.edit().putString(KEY_USER_JSON, json).apply()
        }
    }

    fun loadUserSession(context: Context): UserResponse? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_USER_JSON, null) ?: return null
        return try {
            val obj = JSONObject(jsonStr)
            UserResponse(
                id = obj.getString("id"),
                fullName = obj.getString("fullName"),
                email = obj.getString("email"),
                contactNumber = obj.getString("contactNumber"),
                address = obj.getString("address"),
                accountType = obj.getString("accountType"),
                isVerified = obj.optBoolean("isVerified", false),
                role = obj.optString("role", ""),
                authToken = obj.optString("authToken", "")
            )
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Generates or retrieves a persistent sequential Request ID like #BL-00001
     * mapped to a given MongoDB / backend ID or sequence.
     */
    fun getFormattedRequestId(context: Context, backendId: String, dateSubmitted: String? = null): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val mapKey = "map_id_$backendId"
        var seq = prefs.getInt(mapKey, 0)
        if (seq == 0) {
            var counter = prefs.getInt(KEY_REQ_COUNTER, 0) + 1
            prefs.edit().putInt(KEY_REQ_COUNTER, counter).putInt(mapKey, counter).apply()
            seq = counter
        }
        val padded = String.format("#BL-%05d", seq)
        return if (!dateSubmitted.isNullOrBlank() && dateSubmitted.length >= 10) {
            // e.g. #BL-00001 · Sep 15, 2026
            val dateStr = formatDisplayDate(dateSubmitted.take(10))
            "$padded · $dateStr"
        } else {
            padded
        }
    }

    fun getShortRequestId(context: Context, backendId: String): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val mapKey = "map_id_$backendId"
        var seq = prefs.getInt(mapKey, 0)
        if (seq == 0) {
            var counter = prefs.getInt(KEY_REQ_COUNTER, 0) + 1
            prefs.edit().putInt(KEY_REQ_COUNTER, counter).putInt(mapKey, counter).apply()
            seq = counter
        }
        return String.format("#BL-%05d", seq)
    }

    private fun formatDisplayDate(isoDate: String): String {
        return try {
            val parts = isoDate.split("-")
            if (parts.size == 3) {
                val year = parts[0]
                val month = when (parts[1]) {
                    "01" -> "Jan"; "02" -> "Feb"; "03" -> "Mar"; "04" -> "Apr"
                    "05" -> "May"; "06" -> "Jun"; "07" -> "Jul"; "08" -> "Aug"
                    "09" -> "Sep"; "10" -> "Oct"; "11" -> "Nov"; "12" -> "Dec"
                    else -> parts[1]
                }
                val day = parts[2].toIntOrNull()?.toString() ?: parts[2]
                "$month $day, $year"
            } else {
                isoDate
            }
        } catch (_: Exception) {
            isoDate
        }
    }
}
