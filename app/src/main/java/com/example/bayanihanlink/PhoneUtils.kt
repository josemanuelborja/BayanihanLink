package com.example.bayanihanlink

/**
 * Validates and formats Philippine phone numbers.
 * Allowed formats:
 * - +639XXXXXXXXX (13 chars starting with +639)
 * - 09XXXXXXXXX (11 chars starting with 09)
 * - 9XXXXXXXXX (10 chars starting with 9)
 */
fun isValidPhilippinePhone(phone: String): Boolean {
    val digits = phone.filter { it.isDigit() }
    return when {
        phone.trim().startsWith("+63") -> digits.length == 12 && digits.startsWith("639")
        phone.trim().startsWith("09") -> digits.length == 11 && digits.startsWith("09")
        digits.length == 10 && digits.startsWith("9") -> true
        else -> false
    }
}

/**
 * Formats a contact number cleanly to +63XXXXXXXXXX format for storage/display without duplicating +63.
 */
fun formatPhilippinePhone(phone: String): String {
    val trimmed = phone.trim()
    val digits = trimmed.filter { it.isDigit() }
    if (trimmed.startsWith("+63")) {
        return if (digits.startsWith("63")) "+$digits" else "+63$digits"
    }
    if (digits.startsWith("63") && digits.length == 12) {
        return "+$digits"
    }
    if (digits.startsWith("09") && digits.length == 11) {
        return "+63" + digits.substring(1)
    }
    if (digits.startsWith("9") && digits.length == 10) {
        return "+63$digits"
    }
    return trimmed
}
