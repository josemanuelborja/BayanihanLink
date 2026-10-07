package com.example.bayanihanlink.donor

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AccentBlue = Color(0xFF2B49CC)
private val LabelGray = Color(0xFF8A8FA3)
private val DoneGreen = Color(0xFF3BB273)

private val unitOptions = listOf("bottles", "packs", "kg", "liters", "pieces", "sets", "boxes")

@Composable
fun OfferAssistanceScreen(
    needId: String,
    needTitle: String = "Typhoon Assistance",
    onBack: () -> Unit = {},
    onViewMyOffers: () -> Unit = {},
    onBackToNeedsBoard: () -> Unit = {}
) {
    val context = LocalContext.current

    // Form fields
    var itemProvided by remember { mutableStateOf("") }
    var quantityAvailable by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf(unitOptions.first()) }
    var description by remember { mutableStateOf("") }
    var preferredContact by remember { mutableStateOf("") }
    var additionalNotes by remember { mutableStateOf("") }

    // Once the offer is submitted, we show the success screen instead of the form.
    var isSubmitted by remember { mutableStateOf(false) }

    val fakeOfferId = "#DO-000089"

    if (isSubmitted) {
        OfferSubmittedScreen(
            offerId = fakeOfferId,
            itemOffered = "$itemProvided ($unit)",
            quantity = "$quantityAvailable $unit",
            relatedRequestId = needId,
            onViewMyOffers = onViewMyOffers,
            onBackToNeedsBoard = onBackToNeedsBoard
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Go back", tint = Color(0xFF1A1A2E))
            }
            Text("Offer Assistance", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            // "Responding to Request" card
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3F7EA), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AccentBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Responding to Request", fontSize = 11.sp, color = AccentBlue)
                    Text("$needId \u00B7 $needTitle", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentBlue)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // "In-kind donation only" note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF8E6), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color(0xFFB4690E), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "This is an in-kind donation. No monetary payment is required or accepted. Donate physical goods only.",
                    fontSize = 12.sp,
                    color = Color(0xFFB4690E)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            FieldLabel("ITEM YOU CAN PROVIDE")
            OutlinedTextField(
                value = itemProvided,
                onValueChange = { itemProvided = it },
                placeholder = { Text("e.g. Drinking Water (1L)") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel("QUANTITY AVAILABLE")
                    OutlinedTextField(
                        value = quantityAvailable,
                        onValueChange = { quantityAvailable = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel("UNIT")
                    UnitDropdown(selectedUnit = unit, onUnitSelected = { unit = it })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FieldLabel("DESCRIPTION / NOTES")
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Brand, size, condition, etc.") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            FieldLabel("PREFERRED CONTACT")
            OutlinedTextField(
                value = preferredContact,
                onValueChange = { preferredContact = it },
                placeholder = { Text("+63 9XX XXX XXXX") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            FieldLabel("ADDITIONAL NOTES")
            OutlinedTextField(
                value = additionalNotes,
                onValueChange = { additionalNotes = it },
                placeholder = { Text("Any other information about your offer...") },
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    // VALIDATION: same step-by-step pattern as the Register screen —
                    // check each field, show the FIRST problem found, and only
                    // continue once everything passes.
                    val errorMessage: String? = when {
                        itemProvided.trim().isEmpty() ->
                            "Please enter the item you can provide."
                        quantityAvailable.trim().isEmpty() ->
                            "Please enter the quantity available."
                        quantityAvailable.toIntOrNull() == null ->
                            "Quantity must be a number."
                        quantityAvailable.toIntOrNull() == 0 ->
                            "Quantity must be more than 0."
                        preferredContact.trim().isEmpty() ->
                            "Please enter a preferred contact number."
                        else -> null
                    }

                    if (errorMessage != null) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    } else {
                        // TODO: send this offer to the backend once the
                        // "donation offers" endpoint exists. For now we just
                        // move on to the success screen.
                        isSubmitted = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Submit Assistance Offer", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = LabelGray)
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = Color(0xFFF7F8FA),
    focusedContainerColor = Color(0xFFF7F8FA),
    unfocusedBorderColor = Color.Transparent,
    focusedBorderColor = AccentBlue
)

@Composable
private fun UnitDropdown(selectedUnit: String, onUnitSelected: (String) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFFF7F8FA), RoundedCornerShape(14.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { isExpanded = true }
                )
                .padding(horizontal = 14.dp)
        ) {
            Text(text = selectedUnit, fontSize = 14.sp, color = Color(0xFF1A1A2E))
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = LabelGray)
        }

        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            unitOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onUnitSelected(option)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

// ---------- Success screen shown after submitting ----------
@Composable
private fun OfferSubmittedScreen(
    offerId: String,
    itemOffered: String,
    quantity: String,
    relatedRequestId: String,
    onViewMyOffers: () -> Unit,
    onBackToNeedsBoard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FC))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFDFF3E3)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DoneGreen, modifier = Modifier.size(44.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Offer Submitted!", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Your assistance offer has been recorded and will be reviewed and coordinated by the administrator.",
            fontSize = 13.sp,
            color = LabelGray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            SummaryRow("Donation Offer ID", offerId)
            SummaryRow("Item Offered", itemOffered)
            SummaryRow("Quantity", quantity)
            SummaryRow("Related Request", relatedRequestId)
            SummaryRow("Status", "Pending Coordination", showDivider = false)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onViewMyOffers,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text("View My Offers", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackToNeedsBoard,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Back to Needs Board", color = AccentBlue, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, showDivider: Boolean = true) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(label, fontSize = 13.sp, color = LabelGray)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A2E))
    }
}