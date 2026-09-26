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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
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
data class NotificationItem(
    val icon: ImageVector,
    val iconBackground: Color,
    val title: String,
    val message: String,
    val timeAgo: String,
    val isUnread: Boolean
)
private fun buildSampleNotifications() = listOf(
    NotificationItem(
        icon = Icons.Default.CheckCircle,
        iconBackground = Color(0xFF3BB273), // green
        title = "Request Verified",
        message = "Your request #BL-000234 has been verified by the administrator and posted to the Needs Board.",
        timeAgo = "2 hours ago",
        isUnread = true
    ),
    NotificationItem(
        icon = Icons.Default.Favorite,
        iconBackground = Color(0xFFF5A623), // orange
        title = "Assistance Offer Received",
        message = "A donor has offered 30 bottles of drinking water for your request #BL-000234.",
        timeAgo = "5 hours ago",
        isUnread = true
    ),
    NotificationItem(
        icon = Icons.Default.ArrowUpward,
        iconBackground = Color(0xFF1A2A80), // navy
        title = "Priority Updated",
        message = "Your request #BL-000234 priority has been updated to CRITICAL by the administrator.",
        timeAgo = "6 hours ago",
        isUnread = false
    ),
    NotificationItem(
        icon = Icons.Default.LocationOn,
        iconBackground = Color(0xFF9C4DCC), // purple
        title = "Coordination Update",
        message = "Your request has been submitted to DSWD Region X for verification and coordination.",
        timeAgo = "Yesterday",
        isUnread = false
    ),
    NotificationItem(
        icon = Icons.Default.Star,
        iconBackground = Color(0xFF3BB273), // green
        title = "Request Completed",
        message = "Your previous request #BL-000210 has been successfully completed. Assistance was distributed.",
        timeAgo = "3 days ago",
        isUnread = false
    ),
    NotificationItem(
        icon = Icons.Default.Verified,
        iconBackground = Color(0xFFAEB2C0), // gray = already read, older item
        title = "Request Verified",
        message = "Your request #BL-000234 has been verified by the administrator and posted to the Needs Board.",
        timeAgo = "Sep 15, 2026",
        isUnread = false
    )
)

@Composable
fun AlertsScreen(
    onNavigateHome: () -> Unit = {},
    onNavigateMyRequest: () -> Unit = {},
    onNavigateProfile: () -> Unit = {}
) {
    var notifications by remember { mutableStateOf(buildSampleNotifications()) }
    val unreadCount = notifications.count { it.isUnread }

    Scaffold(
        bottomBar = {
            AlertsBottomBar(
                onHomeClick = onNavigateHome,
                onMyRequestClick = onNavigateMyRequest,
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
            // Title row: "Notifications" on the left, "Mark all as read" on the right.
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
                            // Turn every notification's isUnread to false.
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
                    NotificationCard(notification = notification)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: NotificationItem) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = BorderGray, shape = RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Colored circle icon
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

        // Small blue dot in the top-right corner = this notification is unread.
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
private fun AlertsBottomBar(
    onHomeClick: () -> Unit,
    onMyRequestClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentBlue, selectedTextColor = AccentBlue)
        )
        NavigationBarItem(
            selected = false,
            onClick = onMyRequestClick,
            icon = { Icon(Icons.Default.Description, contentDescription = "My Request") },
            label = { Text("My Request") },
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