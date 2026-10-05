package com.example.bayanihanlink

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val AccentBlue = Color(0xFF2B49CC)
private val LabelGray = Color(0xFF8A8FA3)

@Composable
fun ChangePasswordScreen(
    userId: String,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var retypePassword by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Go back", tint = Color(0xFF1A1A2E))
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Change Password", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Please choose a stronger password. It needs to have at least 6 characters, including both letters and numbers.",
                fontSize = 13.sp,
                color = LabelGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            PasswordField("Current password", currentPassword) { currentPassword = it }
            Spacer(modifier = Modifier.height(12.dp))
            PasswordField("New password", newPassword) { newPassword = it }
            Spacer(modifier = Modifier.height(12.dp))
            PasswordField("Re-type new password", retypePassword) { retypePassword = it }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "To keep your account secure, you'll need to wait 24 hours before making another change.",
                fontSize = 11.sp,
                color = AccentBlue
            )

            Spacer(modifier = Modifier.height(32.dp))

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
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text(if (isSaving) "Please wait..." else "Change Password", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun PasswordField(placeholder: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF7F8FA),
            focusedContainerColor = Color(0xFFF7F8FA),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = AccentBlue
        ),
        modifier = Modifier.fillMaxWidth()
    )
}