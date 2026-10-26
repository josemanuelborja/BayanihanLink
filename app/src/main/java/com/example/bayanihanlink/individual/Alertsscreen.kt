package com.example.bayanihanlink.individual

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bayanihanlink.ui.theme.AppColors

private val AccentBlue: Color
    @Composable @ReadOnlyComposable get() = AppColors.accentBlue
private val LabelGray: Color
    @Composable @ReadOnlyComposable get() = AppColors.labelGray
private val BorderGray: Color
    @Composable @ReadOnlyComposable get() = AppColors.borderGray

/**
 * One notification shown in the list.
 * "notificationId" is the backend's notification id, used to mark it as read.
 */
data class NotificationItem(
    val icon: ImageVector,
    val iconBackground: Color,
    val title: String,
    val message: String,
    val timeAgo: String,
    val isUnread: Boolean,
    val requestId: String? = null,
    val type: String? = null,
    val notificationId: String? = null
)

@Composable
fun AlertsScreen(
    notificationsList: List<NotificationItem> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
    onNotificationClick: (NotificationItem) -> Unit = {},
    onMarkAllRead: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateMyRequest: () -> Unit = {},
    onNavigateProfile: () -> Unit = {}
) {
    // How many notifications are still unread — shown in the subtitle line.
    val unreadCount = notificationsList.count { it.isUnread }

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
                .background(AppColors.background)
        ) {
            // Title row: "Notifications" on the left, "Mark all as read" on the right.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Notifications",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary
                )
                if (unreadCount > 0) {
                    Text(
                        text = "Mark all as read",
                        color = AccentBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onMarkAllRead() }
                    )
                }
            }

            Text(
                text = "$unreadCount unread notifications",
                color = if (unreadCount > 0) AccentBlue else LabelGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> LoadingState()
                errorMessage != null -> ErrorState(message = errorMessage, onRetry = onRetry)
                notificationsList.isEmpty() -> EmptyState()
                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    notificationsList.forEach { notification ->
                        NotificationCard(
                            notification = notification,
                            onClick = { onNotificationClick(notification) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp)
    ) {
        CircularProgressIndicator(color = AccentBlue, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text("Loading notifications…", fontSize = 13.sp, color = LabelGray)
    }
}

@Composable
private fun EmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            tint = LabelGray,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text("No notifications yet", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppColors.textPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Updates about your requests will appear here.",
            fontSize = 13.sp,
            color = LabelGray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 40.dp)
    ) {
        Text(
            text = message,
            fontSize = 13.sp,
            color = LabelGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text("Try Again", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun NotificationCard(notification: NotificationItem, onClick: () -> Unit = {}) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(width = 1.dp, color = BorderGray, shape = RoundedCornerShape(16.dp))
                .background(AppColors.surface, RoundedCornerShape(16.dp))
                .clickable { onClick() }
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
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AppColors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = notification.message, fontSize = 13.sp, color = AppColors.textSecondary)
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

// Bottom nav bar for this screen. "Alerts" is shown as selected since we're on this screen.
@Composable
private fun AlertsBottomBar(
    onHomeClick: () -> Unit,
    onMyRequestClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(containerColor = AppColors.surface) {
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
