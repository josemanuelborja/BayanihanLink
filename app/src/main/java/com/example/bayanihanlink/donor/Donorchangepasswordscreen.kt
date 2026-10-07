package com.example.bayanihanlink.donor

import com.example.bayanihanlink.api.ChangePasswordRequest
import com.example.bayanihanlink.api.RetrofitClient
import com.example.bayanihanlink.api.readableErrorMessage
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Donor version of the Change Password screen.
// It looks the same as the individual one (individual/Changepasswordscreen.kt),
// only the API call is done for a donor account id.

private val AccentBlue = Color(0xFF2B49CC)
private val NoteBg = Color(0xFFEFF1FF)

@Composable
fun DonorChangePasswordScreen(
    userId: String,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var retypePassword by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Small chevron used as the back button (matches the reference).
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "Go back",
            tint = Color(0xFF1A1A2E),
            modifier = Modifier
                .size(18.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBack
                )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Change Password", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Please choose a stronger password. It needs to have at least 6 characters, including both letters and numbers.",
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = Color(0xFF1A1A2E)
        )

        Spacer(modifier = Modifier.height(24.dp))

        PasswordField("Current password", currentPassword) { currentPassword = it }
        Spacer(modifier = Modifier.height(14.dp))
        PasswordField("New password", newPassword) { newPassword = it }
        Spacer(modifier = Modifier.height(14.dp))
        PasswordField("Re-type new password", retypePassword) { retypePassword = it }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "To keep your account secure, you'll need to wait 24 hours before making another change.",
            fontSize = 11.sp,
            color = AccentBlue,
            modifier = Modifier
                .fillMaxWidth()
                .background(NoteBg, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        )

        Spacer(modifier = Modifier.height(120.dp))

        Button(
            onClick = {
                val errorMessage: String? = when {
                    currentPassword.isEmpty() -> "Please enter your current password."
                    newPassword.isEmpty() -> "Please enter a new password."
                    newPassword.length < 6 -> "New password must be at least 6 characters."
                    retypePassword != newPassword -> "Passwords do not match."
                    else -> null
                }

                if (errorMessage != null) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                } else {
                    isSaving = true
                    coroutineScope.launch {
                        try {
                            RetrofitClient.api.changePassword(
                                ChangePasswordRequest(
                                    userId = userId,
                                    currentPassword = currentPassword,
                                    newPassword = newPassword
                                )
                            )
                            Toast.makeText(context, "Password changed successfully!", Toast.LENGTH_LONG).show()
                            onBack()
                        } catch (error: Exception) {
                            Toast.makeText(context, readableErrorMessage(error), Toast.LENGTH_LONG).show()
                        } finally {
                            isSaving = false
                        }
                    }
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentBlue,
                disabledContainerColor = AccentBlue.copy(alpha = 0.6f)
            )
        ) {
            Text(
                if (isSaving) "Please wait..." else "Change Password",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PasswordField(placeholder: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, fontSize = 13.sp, color = Color(0xFF8A8FA3)) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF1F3F8),
            focusedContainerColor = Color(0xFFF1F3F8),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = AccentBlue,
            focusedTextColor = Color(0xFF1A1A2E),
            unfocusedTextColor = Color(0xFF1A1A2E)
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
