package com.example.bayanihanlink

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val AccentBlue = Color(0xFF2B49CC)
private val LabelGray = Color(0xFF8A8FA3)

@Composable
fun EditProfileScreen(
    userId: String,
    user: UserProfile,
    onBack: () -> Unit = {},
    onSaved: (UserResponse) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf(user.fullName) }
    var email by remember { mutableStateOf(user.email) }
    var contactNumber by remember { mutableStateOf(user.contactNumber) }
    var address by remember { mutableStateOf(user.address) }
    var isSaving by remember { mutableStateOf(false) }

    val initials = remember(user.fullName) {
        user.fullName.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header (same look as the Profile screen)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(AccentBlue)
                .padding(top = 32.dp, bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier.size(84.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(user.fullName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier.background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(user.accountType, color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            EditField("FULL NAME", fullName, { fullName = it })
            Spacer(modifier = Modifier.height(16.dp))
            EditField("EMAIL ADDRESS", email, { email = it }, keyboardType = KeyboardType.Email)
            Spacer(modifier = Modifier.height(16.dp))
            EditField("CONTACT NUMBER", contactNumber, { contactNumber = it }, keyboardType = KeyboardType.Phone)
            Spacer(modifier = Modifier.height(16.dp))
            EditField("ADDRESS/LOCATION", address, { address = it })

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Back", color = AccentBlue, fontWeight = FontWeight.SemiBold)
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
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text(if (isSaving) "Saving..." else "Save Changes", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}


@Composable
private fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = LabelGray)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            trailingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = LabelGray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
}