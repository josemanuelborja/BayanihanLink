package com.example.bayanihanlink

import com.example.bayanihanlink.api.RetrofitClient
import com.example.bayanihanlink.api.UpdateProfileRequest
import com.example.bayanihanlink.api.UserResponse
import com.example.bayanihanlink.api.readableErrorMessage
import com.example.bayanihanlink.individual.ProfileHeader
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val AccentBlue = Color(0xFF2B49CC)
private val FieldBg = Color(0xFFF1F3F8)
private val LabelGray = Color(0xFF8A8FA3)
private val ButtonCorner = 12.dp

@Composable
fun EditProfileScreen(
    userId: String,
    user: UserProfile,
    onBack: () -> Unit = {},
    // Called with the FRESH user data once the backend confirms the save
    // worked — MainActivity uses this to update what's shown everywhere else.
    onSaved: (UserResponse) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf(user.fullName) }
    var email by remember { mutableStateOf(user.email) }
    var contactNumber by remember { mutableStateOf(user.contactNumber) }
    var isSaving by remember { mutableStateOf(false) }

    // The address can't be changed here, so it always comes from the user we
    // already have instead of from a text field.
    val address = user.address

    // The Save button only turns on once something is actually different
    // from what the profile already shows.
    val hasChanges = fullName.trim() != user.fullName.trim() ||
        email.trim() != user.email.trim() ||
        contactNumber.trim() != user.contactNumber.trim()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header (same look as the Profile screen)
        ProfileHeader(user = user, onEditProfile = {})

        // The form card is pulled up so it overlaps the blue header (like the design).
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-32).dp)
                .background(Color(0xFFF8F9FC), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            EditField("FULL NAME", fullName, { fullName = it })
            Spacer(modifier = Modifier.height(16.dp))
            EditField("EMAIL ADDRESS", email, { email = it }, keyboardType = KeyboardType.Email)
            Spacer(modifier = Modifier.height(16.dp))
            EditField("CONTACT NUMBER", contactNumber, { contactNumber = it }, keyboardType = KeyboardType.Phone)
            Spacer(modifier = Modifier.height(16.dp))
            // The address is read-only, so this field has no onValueChange.
            EditField("ADDRESS/LOCATION", address, {}, enabled = false)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(ButtonCorner),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue)
            ) {
                Text("Back", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = {
                    // Simple validation, same step-by-step pattern as Register.
                    val errorMessage: String? = when {
                        fullName.trim().isEmpty() -> "Please enter your full name."
                        email.trim().isEmpty() -> "Please enter your email."
                        !email.contains("@") || !email.contains(".") -> "Please enter a valid email."
                        contactNumber.trim().isEmpty() -> "Please enter your contact number."
                        address.trim().isEmpty() -> "Please enter your address."
                        else -> null
                    }

                    if (errorMessage != null) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    } else {
                        isSaving = true
                        coroutineScope.launch {
                            try {
                                val updated = RetrofitClient.api.updateProfile(
                                    id = userId,
                                    request = UpdateProfileRequest(
                                        fullName = fullName.trim(),
                                        email = email.trim(),
                                        contactNumber = contactNumber.trim(),
                                        address = address.trim()
                                    )
                                )
                                Toast.makeText(context, "Profile updated!", Toast.LENGTH_LONG).show()
                                onSaved(updated)
                            } catch (error: Exception) {
                                Toast.makeText(context, readableErrorMessage(error), Toast.LENGTH_LONG).show()
                            } finally {
                                isSaving = false
                            }
                        }
                    }
                },
                enabled = hasChanges && !isSaving,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(ButtonCorner),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    disabledContainerColor = Color(0xFFD5D8E4),
                    disabledContentColor = Color(0xFF9A9EB0)
                )
            ) {
                Text(if (isSaving) "Saving..." else "Save Changes", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    Column {
        Text(label, fontSize = 11.sp, color = LabelGray)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            // The pencil only shows on the fields the user can actually edit.
            trailingIcon = {
                if (enabled) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = LabelGray, modifier = Modifier.size(14.dp))
                }
            },
            singleLine = true,
            enabled = enabled,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = FieldBg,
                focusedContainerColor = FieldBg,
                disabledContainerColor = Color(0xFFEDEFF4),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = AccentBlue,
                disabledBorderColor = Color.Transparent,
                unfocusedTextColor = Color(0xFF1A1A2E),
                focusedTextColor = Color(0xFF1A1A2E),
                disabledTextColor = LabelGray
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
