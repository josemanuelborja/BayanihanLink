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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
private val MintBg = Color(0xFFEFFAF3)
private val BorderGray = Color(0xFFE3E5EC)

// The 3 stages a donation offer can be in.
private enum class OfferStatus { COORDINATING, DISTRIBUTED, COMPLETED }

// One row in the offers list.
private data class DonationOffer(
    val id: String,
    val itemName: String,
    val quantity: String,
    val relatedRequestId: String,
    val date: String,
    val status: OfferStatus,
    val coordinatorNote: String? = null
)

private val sampleOffers = listOf(
    DonationOffer(
        id = "#DO-000089",
        itemName = "Drinking Water (1L)",
        quantity = "30 bottles",
        relatedRequestId = "#BL-000234",
        date = "Sep 16, 2026",
        status = OfferStatus.COORDINATING,
        coordinatorNote = "Administrator is coordinating your offer with DSWD for verification."
    ),
    DonationOffer(
        id = "#DO-000071",
        itemName = "Rice (5kg sacks)",
        quantity = "30 bottles",
        relatedRequestId = "#BL-000189",
        date = "Sep 9, 2026",
        status = OfferStatus.DISTRIBUTED
    ),
    DonationOffer(
        id = "#DO-000055",
        itemName = "Canned Goods",
        quantity = "50 pieces",
        relatedRequestId = "#BL-000140",
        date = "Aug 25, 2026",
        status = OfferStatus.COMPLETED
    )
)

@Composable
fun MyOffersScreen(
    onNavigateNeeds: () -> Unit = {},
    onNavigateAlerts: () -> Unit = {},
    onNavigateProfile: () -> Unit = {}
) {
    val totalOffers = sampleOffers.size
    val activeOffers = sampleOffers.count { it.status == OfferStatus.COORDINATING }
    val completedOffers = sampleOffers.count {
        it.status == OfferStatus.DISTRIBUTED || it.status == OfferStatus.COMPLETED
    }

    Scaffold(
        bottomBar = {
            MyOffersBottomBar(
                onNeedsClick = onNavigateNeeds,
                onAlertsClick = onNavigateAlerts,
                onProfileClick = onNavigateProfile
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Blue header with the title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AccentBlue, RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .padding(vertical = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "My Assistance Offers",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3 stat boxes
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                StatBox(value = totalOffers.toString(), label = "Total Offers", valueColor = AccentBlue, modifier = Modifier.weight(1f))
                StatBox(value = activeOffers.toString(), label = "Active", valueColor = Color(0xFF3BB273), modifier = Modifier.weight(1f))
                StatBox(value = completedOffers.toString(), label = "Completed", valueColor = Color(0xFFF5A623), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                sampleOffers.forEach { offer ->
                    OfferCard(offer)
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun StatBox(value: String, label: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
            .background(Color.White, RoundedCornerShape(14.dp))
            .padding(vertical = 16.dp)
    ) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = valueColor)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 12.sp, color = LabelGray)
    }
}

@Composable
private fun OfferCard(offer: DonationOffer) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(offer.id, fontSize = 12.sp, color = LabelGray)
            StatusPill(offer.status)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(offer.itemName, fontSize = 15.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MintBg, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            DetailColumn("Qty", offer.quantity, Modifier.weight(1f))
            DetailColumn("Request", offer.relatedRequestId, Modifier.weight(1f))
            DetailColumn("Date", offer.date, Modifier.weight(1f))
        }

        if (offer.coordinatorNote != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF3E5F5), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFF8E24AA),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(offer.coordinatorNote, fontSize = 12.sp, color = Color(0xFF8E24AA))
            }
        }
    }
}


@Composable
private fun DetailColumn(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, color = LabelGray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A2E))
    }
}

@Composable
private fun StatusPill(status: OfferStatus) {
    val (background, textColor, label) = when (status) {
        OfferStatus.COORDINATING -> Triple(Color(0xFFF3E5F5), Color(0xFF8E24AA), "Coordinating")
        OfferStatus.DISTRIBUTED -> Triple(Color(0xFFDFF3E3), Color(0xFF2E7D32), "Distributed")
        OfferStatus.COMPLETED -> Triple(Color(0xFFDFF3E3), Color(0xFF2E7D32), "Completed")
    }
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MyOffersBottomBar(
    onNeedsClick: () -> Unit,
    onAlertsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = false,
            onClick = onNeedsClick,
            icon = { Icon(Icons.Default.GridView, contentDescription = "Needs") },
            label = { Text("Needs") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentBlue, selectedTextColor = AccentBlue)
        )
        NavigationBarItem(
            selected = true,
            onClick = { /* already on this screen */ },
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "My Offers") },
            label = { Text("My Offers") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentBlue, selectedTextColor = AccentBlue)
        )
        NavigationBarItem(
            selected = false,
            onClick = onAlertsClick,
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Alerts") },
            label = { Text("Alerts") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentBlue, selectedTextColor = AccentBlue)
        )
        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentBlue, selectedTextColor = AccentBlue)
        )
    }
}