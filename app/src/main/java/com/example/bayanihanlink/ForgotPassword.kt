package com.example.bayanihanlink

import android.widget.Toast
import com.example.bayanihanlink.api.RetrofitClient
import com.example.bayanihanlink.api.SendResetCodeRequest
import com.example.bayanihanlink.api.readableErrorMessage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val ForgotBlue = Color(0xFF2B49CC)
private val FieldBackground = Color(0xFFF3F4F8)
private val LabelGray = Color(0xFF8A8FA3)
private val DarkText = Color(0xFF1A1A2E)

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit = {},
    onContinue: (email: String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSentDialog by remember { mutableStateOf(false) }

    // Simple validation, same step-by-step pattern as Login and Register.
    val errorMessage: String? = when {
        email.trim().isEmpty() -> "Please enter your email."
        !email.contains("@") || !email.contains(".") -> "Please enter a valid email."
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        ForgotTopBar(title = "Forgot Password", onBack = onBack)

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(12.dp))

            ForgotLabel("EMAIL ADDRESS")

            ForgotField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Enter your email",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(20.dp))

            ForgotButton(
                text = "Submit",
                isLoading = isLoading,
                // The button turns off until the email looks okay.
                enabled = errorMessage == null,
                onClick = {
                    if (errorMessage != null) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    } else {
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                // The backend answers "No account found with that
                                // email." if this email isn't registered, and that
                                // message is shown as a Toast.
                                RetrofitClient.api.sendResetCode(SendResetCodeRequest(email.trim()))
                                isLoading = false
                                showSentDialog = true
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

    // The "Check your email" popup shown after the code is sent.
    if (showSentDialog) {
        AlertDialog(
            onDismissRequest = { showSentDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = DarkText,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "Check your email",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Please check your inbox for and input the 6 digit code given to securely reset your password.",
                    fontSize = 12.sp,
                    color = DarkText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSentDialog = false
                        onContinue(email.trim())
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForgotBlue)
                ) {
                    Text("Check email", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/** The top bar: a back arrow on the left, then the title. */
@Composable
fun ForgotTopBar(title: String, onBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go back",
                tint = DarkText,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkText
        )
    }
}

/** The small gray label above each field ("EMAIL ADDRESS", "PASSWORD"...). */
@Composable
fun ForgotLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        color = LabelGray,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

/** One input box. The same style everywhere so the screens stay consistent. */
@Composable
fun ForgotField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, fontSize = 13.sp, color = LabelGray) },
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null, tint = LabelGray, modifier = Modifier.size(18.dp))
        },
        trailingIcon = trailingIcon,
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = FieldBackground,
            focusedContainerColor = FieldBackground,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = ForgotBlue,
            unfocusedTextColor = DarkText,
            focusedTextColor = DarkText
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

/** The big blue button. Shows a spinner while it's working. */
@Composable
fun ForgotButton(
    text: String,
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ForgotBlue,
            disabledContainerColor = Color(0xFFAEB7E4),
            disabledContentColor = Color.White
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
