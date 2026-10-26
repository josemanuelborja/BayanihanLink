package com.example.bayanihanlink

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private val AccentBlue = Color(0xFF2B49CC)
private val LabelGray = Color(0xFF8A8FA3)
private val DarkText = Color(0xFF1A1A2E)

/**
 * Checks whether the device has a fingerprint/biometric sensor available.
 */
fun isBiometricAvailable(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    return when {
        ContextCompat.checkSelfPermission(context, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED -> false
        else -> biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
    }
}

/**
 * Screen that prompts the user for fingerprint authentication.
 * If biometrics are unavailable, it shows a fallback button to continue without fingerprint.
 */
@Composable
fun BiometricScreen(
    context: Context,
    onAuthenticated: () -> Unit,
    onSkip: () -> Unit
) {
    val authenticating = remember { mutableStateOf(false) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    val canUseBiometrics = remember { isBiometricAvailable(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(40.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFF7F8FA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (canUseBiometrics) Icons.Default.Fingerprint else Icons.Default.LockOpen,
                contentDescription = null,
                tint = if (canUseBiometrics) AccentBlue else LabelGray,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (canUseBiometrics) "Fingerprint Login" else "Biometric Unavailable",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (canUseBiometrics) {
                "Place your finger on the sensor to sign in."
            } else {
                "This device does not support fingerprint authentication."
            },
            fontSize = 14.sp,
            color = LabelGray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (canUseBiometrics) {
            Button(
                onClick = {
                    authenticating.value = true
                    val activity = context as androidx.fragment.app.FragmentActivity
                    val prompt = BiometricPrompt(
                        activity,
                        executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationError(errorCode: Int, errorString: CharSequence) {
                                super.onAuthenticationError(errorCode, errorString)
                                Handler(Looper.getMainLooper()).post {
                                    authenticating.value = false
                                    Toast.makeText(context, "Authentication failed: $errorString", Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                super.onAuthenticationSucceeded(result)
                                Handler(Looper.getMainLooper()).post {
                                    authenticating.value = false
                                    onAuthenticated()
                                }
                            }

                            override fun onAuthenticationFailed() {
                                super.onAuthenticationFailed()
                                Handler(Looper.getMainLooper()).post {
                                    authenticating.value = false
                                    Toast.makeText(context, "Authentication failed. Please try again.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                    prompt.authenticate(
                        BiometricPrompt.PromptInfo.Builder()
                            .setTitle("BayanihanLink")
                            .setSubtitle("Use your fingerprint to sign in")
                            .build()
                    )
                },
                enabled = !authenticating.value,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text(
                    text = if (authenticating.value) "Authenticating..." else "Use Fingerprint",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            androidx.compose.material3.OutlinedButton(
                onClick = onSkip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(1.dp, AccentBlue, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue)
            ) {
                Text("Continue without Fingerprint", fontWeight = FontWeight.SemiBold)
            }
        } else {
            Button(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Continue to Login", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
