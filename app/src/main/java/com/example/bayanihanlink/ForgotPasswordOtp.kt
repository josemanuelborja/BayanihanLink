package com.example.bayanihanlink

import android.widget.Toast
import com.example.bayanihanlink.api.RetrofitClient
import com.example.bayanihanlink.api.VerifyResetCodeRequest
import com.example.bayanihanlink.api.readableErrorMessage
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val CodeBoxBackground = Color(0xFFF3F4F8)
private val CodeBoxBorder = Color(0xFFE3E5EC)
private val CodeDarkText = Color(0xFF1A1A2E)

@Composable
fun ForgotPasswordOtpScreen(
    email: String,
    onBack: () -> Unit = {},
    onContinue: (code: String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // The code has to be exactly 6 digits.
    val errorMessage: String? = when {
        code.isEmpty() -> "Please enter the code."
        code.length != 6 -> "The code must be 6 digits."
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

            ForgotLabel("ENTER CODE")

            Box(modifier = Modifier.fillMaxWidth()) {
                // Arrangement.CenterHorizontally keeps the boxes in the middle,
                // and weight(1f) makes all 6 boxes share the width evenly.
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (index in 0 until 6) {
                        // If the user typed more digits than boxes, show the last ones.
                        val digit = if (index < code.length) code[index].toString() else ""
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .background(CodeBoxBackground, RoundedCornerShape(12.dp))
                                .border(1.dp, CodeBoxBorder, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = digit, fontSize = 18.sp, color = CodeDarkText)
                        }
                    }
                }

                // Invisible field on top — this is what the user types into.
                OutlinedTextField(
                    value = code,
                    onValueChange = { newValue ->
                        // Keep only digits, and never more than 6 of them.
                        code = newValue.filter { it.isDigit() }.take(6)
                    },
                    singleLine = true,
                    // The typed text is invisible; the boxes above show it instead.
                    textStyle = TextStyle(color = Color.Transparent),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ForgotButton(
                text = "Submit",
                isLoading = isLoading,
                enabled = errorMessage == null,
                onClick = {
                    if (errorMessage != null) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    } else {
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                RetrofitClient.api.verifyResetCode(VerifyResetCodeRequest(email, code))
                                isLoading = false
                                onContinue(code)
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
}
