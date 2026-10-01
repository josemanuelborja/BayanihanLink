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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AccentBlue = Color(0xFF2B49CC)
private val LabelGray = Color(0xFF8A8FA3)
private val BorderGray = Color(0xFFE3E5EC)

// One notification shown in the list.
private data class DonorNotification(
    val icon: ImageVector,
    val iconBackground: Color,
    val title: String,
    val message: String,
    val timeAgo: String,
    val isUnread: Boolean
)
private fun buildSampleDonorNotifications() = listOf(
    DonorNotification(
        icon = Icons.Default.CheckCircle,
        iconBackground = Color(0xFF3BB273), // green
        title = "Offer Accepted",
        message = "Your assistance offer for Drinking Water (L) was accepted by the requester.",
        timeAgo = "2 hours ago",
        isUnread = true
    ),
    DonorNotification(
        icon = Icons.Default.Favorite,
        iconBackground = Color(0xFFF5A623), // orange
        title = "Coordination Update",
        message = "The requester has updated the coordination details for your Rice (5kg sacks) donation.",
        timeAgo = "5 hours ago",
        isUnread = true
    ),
    DonorNotification(
        icon = Icons.Default.ArrowUpward,
        iconBackground = Color(0xFF1A2A80), // navy
        title = "Donation Reminder",
        message = "Your accepted offer for Blankets & Clothing is awaiting fulfillment.",
        timeAgo = "6 hours ago",
        isUnread = false
    ),
    DonorNotification(
        icon = Icons.Default.Flag,
        iconBackground = Color(0xFF9C4DCC), // purple
        title = "Request Cancelled",
        message = "The request for Food Assistance has been cancelled. Your offer is no longer needed.",
        timeAgo = "Yesterday",
        isUnread = false
    ),
    DonorNotification(
        icon = Icons.Default.Settings,
        iconBackground = Color(0xFFAEB2C0), // gray = already read, older item
        title = "Donation Completed",
        message = "Your donation of Hygiene Supplies has been marked as completed. Thank you for helping!",
        timeAgo = "Sep 15, 2026",
        isUnread = false
    )
)

@Composable
fun DonorAlertsScreen(
    onNavigateNeeds: () -> Unit = {},
    onNavigateMyOffers: () -> Unit = {},
    onNavigateProfile: () -> Unit = {}
) {

    var notifications by remember { mutableStateOf(buildSampleDonorNotifications()) }

    val unreadCount = notifications.count { it.isUnread }

    Scaffold(
        bottomBar = {
            DonorAlertsBottomBar(
                onNeedsClick = onNavigateNeeds,
                onMyOffersClick = onNavigateMyOffers,
                onProfileClick = onNavigateProfile
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(text = "Notifications", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Mark all as read",
                    color = AccentBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            notifications = notifications.map { it.copy(isUnread = false) }
                        }
                    )
                )
            }

            Text(
                text = "$unreadCount unread notifications",
                color = if (unreadCount > 0) AccentBlue else LabelGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                notifications.forEach { notification ->
                    DonorNotificationCard(notification = notification)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DonorNotificationCard(notification: DonorNotification) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(notification.iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = notification.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = notification.message, fontSize = 13.sp, color = Color(0xFF5A5F73))
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = notification.timeAgo, fontSize = 11.sp, color = LabelGray)
            }
        }

        if (notification.isUnread) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-10).dp, y = 10.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(AccentBlue)
            )
        }
    }
}

@Composable
private fun DonorAlertsBottomBar(
    onNeedsClick: () -> Unit,
    onMyOffersClick: () -> Unit,
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
            selected = false,
            onClick = onMyOffersClick,
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "My Offers") },
            label = { Text("My Offers") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentBlue, selectedTextColor = AccentBlue)
        )
        NavigationBarItem(
            selected = true,
            onClick = { /* already on this screen */ },
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