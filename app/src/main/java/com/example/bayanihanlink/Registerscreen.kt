package com.example.bayanihanlink

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Same accent color used on the Login and Onboarding screens, so everything matches.
private val AccentBlue = Color(0xFF2B3FC7)
private val FieldBackground = Color(0xFFF7F8FA)
private val FieldBorder = Color(0xFFE3E5EC)
private val LabelGray = Color(0xFF8A8FA3)

enum class AccountType {
    AFFECTED_INDIVIDUAL,
    DONOR_VOLUNTEER
}

data class RegisterFormData(
    val fullName: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val contactNumber: String,
    val address: String,
    val accountType: AccountType?
)

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegister: (RegisterFormData) -> Unit = {},
    onLogIn: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedAccountType by remember { mutableStateOf<AccountType?>(null) }
    var agreedToTerms by remember { mutableStateOf(false) }

    // A Column that scrolls, since this form is taller than most phone screens.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar: back arrow + "Create Account" title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go back",
                    tint = Color(0xFF1A1A2E)
                )
            }
            Text(
                text = "Create Account",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            FormField(
                label = "FULL NAME",
                value = fullName,
                onValueChange = { fullName = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            FormField(
                label = "EMAIL ADDRESS",
                value = email,
                onValueChange = { email = it },
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(18.dp))

            FormField(
                label = "PASSWORD",
                value = password,
                onValueChange = { password = it },
                isPassword = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            FormField(
                label = "CONFIRM PASSWORD",
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                isPassword = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            FormField(
                label = "CONTACT NUMBER",
                value = contactNumber,
                onValueChange = { contactNumber = it },
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(18.dp))

            FormField(
                label = "ADDRESS/LOCATION",
                value = address,
                onValueChange = { address = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "ACCOUNT TYPE",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = LabelGray
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Two side-by-side cards. Tapping one selects it and un-selects the other.
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AccountTypeCard(
                    title = "Affected Individual",
                    subtitle = "I need assistance",
                    isSelected = selectedAccountType == AccountType.AFFECTED_INDIVIDUAL,
                    onClick = { selectedAccountType = AccountType.AFFECTED_INDIVIDUAL },
                    modifier = Modifier.weight(1f)
                )
                AccountTypeCard(
                    title = "Donor / Volunteer",
                    subtitle = "I want to help",
                    isSelected = selectedAccountType == AccountType.DONOR_VOLUNTEER,
                    onClick = { selectedAccountType = AccountType.DONOR_VOLUNTEER },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = agreedToTerms,
                    onCheckedChange = { agreedToTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = AccentBlue)
                )

                val agreementText = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF5A5F73))) {
                        append("I agree to the ")
                    }
                    // "TERMS" tag marks this part as clickable and links it to onTermsClick
                    pushStringAnnotation(tag = "TERMS", annotation = "terms")
                    withStyle(SpanStyle(color = AccentBlue, fontWeight = FontWeight.SemiBold)) {
                        append("Terms and Condition")
                    }
                    pop()
                    withStyle(SpanStyle(color = Color(0xFF5A5F73))) {
                        append(" and ")
                    }
                    // "PRIVACY" tag marks this part as clickable and links it to onPrivacyClick
                    pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                    withStyle(SpanStyle(color = AccentBlue, fontWeight = FontWeight.SemiBold)) {
                        append("Privacy Policy")
                    }
                    pop()
                    withStyle(SpanStyle(color = Color(0xFF5A5F73))) {
                        append(" of BayanihanLink.")
                    }
                }

                ClickableText(
                    text = agreementText,
                    style = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, lineHeight = 18.sp),
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .weight(1f),
                    onClick = { offset ->
                        agreementText.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                            .firstOrNull()?.let { onTermsClick() }
                        agreementText.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                            .firstOrNull()?.let { onPrivacyClick() }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onRegister(
                        RegisterFormData(
                            fullName = fullName,
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword,
                            contactNumber = contactNumber,
                            address = address,
                            accountType = selectedAccountType
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Register", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            val loginText = buildAnnotatedString {
                withStyle(SpanStyle(color = LabelGray)) {
                    append("Already have an account? ")
                }
                withStyle(SpanStyle(color = AccentBlue, fontWeight = FontWeight.SemiBold)) {
                    append("Log in")
                }
            }
            Text(
                text = loginText,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onLogIn
                    ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// One text field with a small gray label above it, styled like the Login screen's fields.
@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = LabelGray
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = FieldBackground,
                focusedContainerColor = FieldBackground,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = AccentBlue
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// One selectable "Affected Individual" / "Donor / Volunteer" card.
@Composable
private fun AccountTypeCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AccentBlue else FieldBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .background(
                color = if (isSelected) Color(0xFFEEF0FC) else Color.White,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = LabelGray
            )
        }
    }
}