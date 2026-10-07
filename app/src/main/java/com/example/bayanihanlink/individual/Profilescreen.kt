package com.example.bayanihanlink.individual

import com.example.bayanihanlink.UserProfile
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sell
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// This is the Profile screen for AFFECTED INDIVIDUAL accounts.
// Donors/Volunteers use the separate DonorProfileScreen.kt instead
// (same look overall, but different bottom tabs and menu items).

// The colors used by the profile screens.
private val AccentBlue = Color(0xFF2B49CC)
private val CardBg = Color(0xFFF8F9FC)
private val LabelGray = Color(0xFF8A8FA3)
private val DarkText = Color(0xFF1A1A2E)
private val BorderGray = Color(0xFFE9EBF2)
private val DangerRed = Color(0xFFD64550)
private val DangerBg = Color(0xFFFDECEE)
private val DangerBorder = Color(0xFFF6C9CE)

@Composable
fun ProfileScreen(
    user: UserProfile,
    onEditProfile: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onMyRequests: () -> Unit = {},
    onAboutBayanihanLink: () -> Unit = {},
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
            ProfileHeader(user = user, onEditProfile = onEditProfile)

            // The card is pulled up so it overlaps the blue header (like the design).
            AccountInfoCard(
                user = user,
                modifier = Modifier.padding(horizontal = 16.dp).offset(y = (-32).dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            StatsRow(user = user)

            Spacer(modifier = Modifier.height(16.dp))

            MenuCard(
                onEditProfile = onEditProfile,
                onChangePassword = onChangePassword,
                onMyRequests = onMyRequests,
                onAboutBayanihanLink = onAboutBayanihanLink
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
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProfileHeader(
    user: UserProfile,
    onEditProfile: () -> Unit,
    badgeText: String = user.accountType
) {
    val initials = remember(user.fullName) {
        user.fullName.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(AccentBlue)
            .padding(top = 28.dp, bottom = 56.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.28f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initials, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            // Small orange pencil badge on the avatar corner.
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(20.dp)
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
                    modifier = Modifier.size(11.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = user.fullName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(text = badgeText, color = Color.White, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AccountInfoCard(user: UserProfile, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardBg, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "ACCOUNT INFORMATION",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = LabelGray
        )
        Spacer(modifier = Modifier.height(14.dp))
        InfoRow(icon = Icons.Default.Person, label = "Full Name", value = user.fullName)
        Spacer(modifier = Modifier.height(14.dp))
        InfoRow(icon = Icons.Default.Email, label = "Email", value = user.email)
        Spacer(modifier = Modifier.height(14.dp))
        InfoRow(icon = Icons.Default.Phone, label = "Contact", value = user.contactNumber)
        Spacer(modifier = Modifier.height(14.dp))
        InfoRow(icon = Icons.Default.Home, label = "Address", value = user.address)
        Spacer(modifier = Modifier.height(14.dp))
        InfoRow(icon = Icons.Default.Sell, label = "Account Type", value = user.accountType)
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = LabelGray)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 13.sp, color = DarkText, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun StatsRow(user: UserProfile) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        StatBox(
            value = user.totalRequests.toString(),
            label = "Total Request",
            background = Color(0xFFEDF1FF),
            valueColor = AccentBlue,
            modifier = Modifier.weight(1f)
        )
        StatBox(
            value = user.verifiedRequests.toString(),
            label = "Verified",
            background = Color(0xFFE8F6EE),
            valueColor = Color(0xFF1E8E4E),
            modifier = Modifier.weight(1f)
        )
        StatBox(
            value = user.completedRequests.toString(),
            label = "Completed",
            background = Color(0xFFE8F6EE),
            valueColor = Color(0xFF1E8E4E),
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
            .background(background, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp)
    ) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 10.sp, color = LabelGray)
    }
}

@Composable
private fun MenuCard(
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onMyRequests: () -> Unit,
    onAboutBayanihanLink: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
    ) {
        MenuRow(icon = Icons.Default.Edit, label = "Edit Profile", onClick = onEditProfile)
        MenuDivider()
        MenuRow(icon = Icons.Default.Lock, label = "Change Password", onClick = onChangePassword)
        MenuDivider()
        MenuRow(icon = Icons.Default.Description, label = "My Requests", onClick = onMyRequests)
        MenuDivider()
        MenuRow(icon = Icons.Default.Info, label = "About BayanihanLink", onClick = onAboutBayanihanLink)
    }
}

@Composable
private fun MenuRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 15.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = label, fontSize = 13.sp, color = DarkText, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = LabelGray,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun MenuDivider() {
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderGray))
}

@Composable
private fun LogOutButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(DangerBg, RoundedCornerShape(12.dp))
            .border(1.dp, DangerBorder, RoundedCornerShape(12.dp))
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
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = LabelGray,
                unselectedTextColor = LabelGray,
                selectedIconColor = AccentBlue,
                selectedTextColor = AccentBlue,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onMyRequestClick,
            icon = { Icon(Icons.Default.Description, contentDescription = "My Request") },
            label = { Text("My Request", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = LabelGray,
                unselectedTextColor = LabelGray,
                selectedIconColor = AccentBlue,
                selectedTextColor = AccentBlue,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onAlertsClick,
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Alerts") },
            label = { Text("Alerts", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = LabelGray,
                unselectedTextColor = LabelGray,
                selectedIconColor = AccentBlue,
                selectedTextColor = AccentBlue,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = true,
            onClick = { /* already on this screen */ },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = LabelGray,
                unselectedTextColor = LabelGray,
                selectedIconColor = AccentBlue,
                selectedTextColor = AccentBlue,
                indicatorColor = Color.Transparent
            )
        )
    }
}
