package com.example.bayanihanlink

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AccentBlue = Color(0xFF2B49CC)
private val LabelGray = Color(0xFF8A8FA3)
private val BorderGray = Color(0xFFE3E5EC)
private val CriticalRed = Color(0xFFE5484D)
private val DoneGreen = Color(0xFF3BB273)
private val OfferOrange = Color(0xFFF5A623)

// Everything shown on this screen for one community need.
data class NeedDetail(
    val id: String,
    val title: String,
    val date: String,
    val urgency: String,       // "CRITICAL", "URGENT", "NORMAL"
    val isVerified: Boolean,
    val disasterType: String,
    val category: String,
    val location: String,
    val itemName: String,
    val neededQuantityLabel: String, // e.g. "100 Bottles"
    val description: String,
    val quantityOffered: Int,
    val quantityTarget: Int
)

private fun sampleNeedDetail(needId: String): NeedDetail = NeedDetail(
    id = needId,
    title = "Typhoon Assistance",
    date = "Sep 15, 2026",
    urgency = "CRITICAL",
    isVerified = true,
    disasterType = "Typhoon",
    category = "Drinking Water",
    location = "Cagayan de Oro City",
    itemName = "Drinking Water (1L Bottles)",
    neededQuantityLabel = "100 Bottles",
    description = "50 families at the CDO Evacuation Center are urgently in need of clean drinking water. The area's water supply was disrupted by Typhoon Carina.",
    quantityOffered = 30,
    quantityTarget = 100
)

@Composable
fun NeedDetailsScreen(
    needId: String,
    onBack: () -> Unit = {},
    onOfferAssistance: () -> Unit = {}
) {
    val detail = sampleNeedDetail(needId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar: back arrow + title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Go back", tint = Color(0xFF1A1A2E))
            }
            Text("Request Details", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        // Red/urgency-colored header card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(CriticalRed, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(detail.urgency, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                if (detail.isVerified) {
                    Text("\u25CF Verified", color = Color.White, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(detail.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text("${detail.id} \u00B7 ${detail.date}", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            // "Request Information" card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text("Request Information", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoPair("Disaster", detail.disasterType, Modifier.weight(1f))
                    InfoPair("Category", detail.category, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoPair("Urgency", detail.urgency.lowercase().replaceFirstChar { it.uppercase() }, Modifier.weight(1f))
                    InfoPair("Location", detail.location, Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Items Needed" card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text("Items Needed", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFBE9EA), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(detail.itemName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Urgently Needed", fontSize = 12.sp, color = LabelGray)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Needed", fontSize = 12.sp, color = LabelGray)
                        Text(detail.neededQuantityLabel, fontSize = 14.sp, color = CriticalRed, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(detail.description, fontSize = 13.sp, color = LabelGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Current Assistance Progress" card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Current Assistance Progress", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "${detail.quantityOffered}/${detail.quantityTarget}",
                        fontSize = 13.sp,
                        color = LabelGray
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                val progress = detail.quantityOffered.toFloat() / detail.quantityTarget.toFloat()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0xFFE3E5EC), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .height(8.dp)
                            .background(DoneGreen, RoundedCornerShape(4.dp))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${detail.quantityOffered} bottles offered so far",
                    fontSize = 12.sp,
                    color = LabelGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF8E6), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFB4690E), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Only general location information is shown to protect the privacy of affected individuals. Personal contact details are not displayed publicly.",
                    fontSize = 12.sp,
                    color = Color(0xFFB4690E)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onOfferAssistance,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OfferOrange)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Offer Assistance", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun InfoPair(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 12.sp, color = LabelGray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A2E))
    }
}