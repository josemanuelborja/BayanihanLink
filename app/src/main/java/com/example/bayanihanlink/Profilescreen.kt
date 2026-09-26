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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AccentBlue = Color(0xFF2B49CC)
private val LabelGray = Color(0xFF8A8FA3)
private val BorderGray = Color(0xFFE3E5EC)
private val DangerRed = Color(0xFFD32F2F)
data class UserProfile(
    val fullName: String,
    val email: String,
    val contactNumber: String,
    val address: String,
    val accountType: String,
    val totalRequests: Int,
    val verifiedRequests: Int,
    val completedRequests: Int
)

private val sampleUser = UserProfile(
    fullName = "Maria Santos",
    email = "maria.santos@email.com",
    contactNumber = "+63 912 345 6789",
    address = "Brgy. 14, Cagayan de Oro City",
    accountType = "Affected Individual",
    totalRequests = 2,
    verifiedRequests = 1,
    completedRequests = 0
)

@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onNotificationPreferences: () -> Unit = {},
    onMyRequests: () -> Unit = {},
    onAboutBayanihanLink: () -> Unit = {},
    onContactSupport: () -> Unit = {},
    onLogOut: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateMyRequest: () -> Unit = {},
    onNavigateAlerts: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            ProfileBottomBar(
                onHomeClick = onNavigateHome,
                onMyRequestClick = onNavigateMyRequest,
                onAlertsClick = onNavigateAlerts
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
            ProfileHeader(user = sampleUser, onEditProfile = onEditProfile)

            Spacer(modifier = Modifier.height(16.dp))

            AccountInfoCard(user = sampleUser)

            Spacer(modifier = Modifier.height(16.dp))

            StatsRow(user = sampleUser)

            Spacer(modifier = Modifier.height(16.dp))

            MenuCard(
                onEditProfile = onEditProfile,
                onChangePassword = onChangePassword,
                onNotificationPreferences = onNotificationPreferences,
                onMyRequests = onMyRequests,
                onAboutBayanihanLink = onAboutBayanihanLink,
                onContactSupport = onContactSupport
            )

            Spacer(modifier = Modifier.height(16.dp))

            LogOutButton(onClick = onLogOut)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "BayanihanLink v1.0.0",
                fontSize = 12.sp,
                color = LabelGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ---------- Top blue header: avatar + name + account type pill ----------
@Composable
private fun ProfileHeader(user: UserProfile, onEditProfile: () -> Unit) {
    // Turn "Maria Santos" into initials "MS" for the avatar circle.
    val initials = remember(user.fullName) {
        user.fullName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(AccentBlue)
            .padding(top = 32.dp, bottom = 24.dp)
    ) {
        Box {
            // Avatar circle with initials
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initials, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            // Small orange "edit" badge on the bottom-right of the avatar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5A623))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onEditProfile
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit profile picture",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = user.fullName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(text = user.accountType, color = Color.White, fontSize = 12.sp)
        }
    }
}

// ---------- "ACCOUNT INFORMATION" card ----------
@Composable
private fun AccountInfoCard(user: UserProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(width = 1.dp, color = BorderGray, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "ACCOUNT INFORMATION",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = LabelGray
        )
        Spacer(modifier = Modifier.height(14.dp))

        InfoRow(label = "Full Name", value = user.fullName)
        Spacer(modifier = Modifier.height(12.dp))
        InfoRow(label = "Email", value = user.email)
        Spacer(modifier = Modifier.height(12.dp))
        InfoRow(label = "Contact", value = user.contactNumber)
        Spacer(modifier = Modifier.height(12.dp))
        InfoRow(label = "Address", value = user.address)
        Spacer(modifier = Modifier.height(12.dp))
        InfoRow(label = "Account Type", value = user.accountType)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 12.sp, color = LabelGray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 14.sp, color = Color(0xFF1A1A2E), fontWeight = FontWeight.Medium)
    }
}

// ---------- 3 stat boxes: Total Request / Verified / Completed ----------
@Composable
private fun StatsRow(user: UserProfile) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        StatBox(
            value = user.totalRequests.toString(),
            label = "Total Request",
            background = Color(0xFFE3E9FF),
            valueColor = AccentBlue,
            modifier = Modifier.weight(1f)
        )
        StatBox(
            value = user.verifiedRequests.toString(),
            label = "Verified",
            background = Color(0xFFDFF3E3),
            valueColor = Color(0xFF2E7D32),
            modifier = Modifier.weight(1f)
        )
        StatBox(
            value = user.completedRequests.toString(),
            label = "Completed",
            background = Color(0xFFDFF3E3),
            valueColor = Color(0xFF2E7D32),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatBox(
    value: String,
    label: String,
    background: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(background, RoundedCornerShape(14.dp))
            .padding(vertical = 14.dp)
    ) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = LabelGray)
    }
}

// ---------- Menu list: Edit Profile, Change Password, etc. ----------
@Composable
private fun MenuCard(
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onNotificationPreferences: () -> Unit,
    onMyRequests: () -> Unit,
    onAboutBayanihanLink: () -> Unit,
    onContactSupport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(width = 1.dp, color = BorderGray, shape = RoundedCornerShape(16.dp))
    ) {
        MenuRow(label = "Edit Profile", onClick = onEditProfile)
        MenuDivider()
        MenuRow(label = "Change Password", onClick = onChangePassword)
        MenuDivider()
        MenuRow(label = "Notification Preferences", onClick = onNotificationPreferences)
        MenuDivider()
        MenuRow(label = "My Requests", onClick = onMyRequests)
        MenuDivider()
        MenuRow(label = "About BayanihanLink", onClick = onAboutBayanihanLink)
        MenuDivider()
        MenuRow(label = "Contact Support", onClick = onContactSupport)
    }
}

@Composable
private fun MenuRow(label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(text = label, fontSize = 14.sp, color = Color(0xFF1A1A2E))
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = LabelGray
        )
    }
}

@Composable
private fun MenuDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BorderGray)
    )
}

// ---------- "Log Out" button ----------
@Composable
private fun LogOutButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(width = 1.dp, color = Color(0xFFF5C6CB), shape = RoundedCornerShape(14.dp))
            .background(Color(0xFFFDEBEE), RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Log Out", color = DangerRed, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}
@Composable
private fun ProfileBottomBar(
    onHomeClick: () -> Unit,
    onMyRequestClick: () -> Unit,
    onAlertsClick: () -> Unit
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
            selected = false,
            onClick = onAlertsClick,
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Alerts") },
            label = { Text("Alerts") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentBlue, selectedTextColor = AccentBlue)
        )
        NavigationBarItem(
            selected = true,
            onClick = { /* already on this screen */ },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentBlue, selectedTextColor = AccentBlue)
        )
    }
}