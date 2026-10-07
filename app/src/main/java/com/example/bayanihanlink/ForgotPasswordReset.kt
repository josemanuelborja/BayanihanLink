package com.example.bayanihanlink

import android.widget.Toast
import com.example.bayanihanlink.api.ResetPasswordRequest
import com.example.bayanihanlink.api.RetrofitClient
import com.example.bayanihanlink.api.readableErrorMessage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val ResetBlue = Color(0xFF2B49CC)
private val ResetDarkText = Color(0xFF1A1A2E)

@Composable
fun ForgotPasswordResetScreen(
    email: String,
    code: String,
    onBack: () -> Unit = {},
    onFinished: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Both boxes must match and be at least 6 characters.
    val errorMessage: String? = when {
        newPassword.isEmpty() -> "Please enter your new password."
        newPassword.length < 6 -> "New password must be at least 6 characters."
        confirmPassword.isEmpty() -> "Please confirm your new password."
        confirmPassword != newPassword -> "Passwords do not match."
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        ForgotTopBar(title = "Reset your password", onBack = onBack)

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(12.dp))

            ForgotLabel("PASSWORD")

            ForgotField(
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = "Enter your new password",
                leadingIcon = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { ShowHideIcon(passwordVisible, onToggle = { passwordVisible = !passwordVisible }) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ForgotLabel("CONFIRM NEW PASSWORD")

            ForgotField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Enter your new password",
                leadingIcon = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { ShowHideIcon(passwordVisible, onToggle = { passwordVisible = !passwordVisible }) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            ForgotButton(
                text = "Reset Password",
                isLoading = isLoading,
                enabled = errorMessage == null,
                onClick = {
                    if (errorMessage != null) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    } else {
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                RetrofitClient.api.resetPassword(ResetPasswordRequest(email, code, newPassword))
                                isLoading = false
                                showSuccessDialog = true
                            } catch (error: Exception) {
                                isLoading = false
                                Toast.makeText(context, readableErrorMessage(error), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            )
        }
    }

    // The "Password Changed!" popup, with a button back to Login.
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFDFF3E3), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF1E8E4E),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Password Changed!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResetDarkText,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Your password has been updated.",
                    fontSize = 12.sp,
                    color = ResetDarkText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = onFinished,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ResetBlue)
                ) {
                    Text("Login", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun ShowHideIcon(passwordVisible: Boolean, onToggle: () -> Unit) {
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = if (passwordVisible) "Hide password" else "Show password",
            tint = Color(0xFF8A8FA3),
            modifier = Modifier.size(18.dp)
        )
    }
}
