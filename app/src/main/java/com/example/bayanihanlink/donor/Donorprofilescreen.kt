package com.example.bayanihanlink.donor

import com.example.bayanihanlink.UserProfile
import com.example.bayanihanlink.individual.ProfileHeader
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// This is the Profile screen for DONOR/VOLUNTEER accounts.
// Affected Individuals use the separate ProfileScreen.kt instead.
// NOTE: "UserProfile" (the data class) is NOT declared here — it's shared,
// so it lives in the root package (see UserProfile.kt) and is imported above.

// The colors used by the profile screens (same values as Profilescreen.kt).
private val AccentBlue = Color(0xFF2B49CC)
private val CardBg = Color(0xFFF8F9FC)
private val LabelGray = Color(0xFF8A8FA3)
private val DarkText = Color(0xFF1A1A2E)
private val BorderGray = Color(0xFFE9EBF2)
private val DangerRed = Color(0xFFD64550)
private val DangerBg = Color(0xFFFDECEE)
private val DangerBorder = Color(0xFFF6C9CE)

@Composable
fun DonorProfileScreen(
    user: UserProfile,
    onEditProfile: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onAboutBayanihanLink: () -> Unit = {},
    onLogOut: () -> Unit = {},
    onNavigateNeeds: () -> Unit = {},
    onNavigateMyOffers: () -> Unit = {},
    onNavigateAlerts: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            DonorProfileBottomBar(
                onNeedsClick = onNavigateNeeds,
                onMyOffersClick = onNavigateMyOffers,
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
            // Donors show a short "Donor" badge instead of the full account type.
            ProfileHeader(
                user = user,
                badgeText = "Donor",
                onEditProfile = onEditProfile
            )

            // The card is pulled up so it overlaps the blue header (like the design).
            DonorAccountInfoCard(
                user = user,
                modifier = Modifier.padding(horizontal = 16.dp).offset(y = (-32).dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            DonorStatsRow(user = user)

            Spacer(modifier = Modifier.height(16.dp))

            // Donors don't have a "My Requests" item — they use "My Offers"
            // from the bottom nav instead, so the menu here is shorter.
            DonorMenuCard(
                onEditProfile = onEditProfile,
                onChangePassword = onChangePassword,
                onAboutBayanihanLink = onAboutBayanihanLink
            )

            Spacer(modifier = Modifier.height(16.dp))

            DonorLogOutButton(onClick = onLogOut)

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
private fun DonorAccountInfoCard(user: UserProfile, modifier: Modifier = Modifier) {
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
        DonorInfoRow(icon = Icons.Default.Person, label = "Full Name", value = user.fullName)
        Spacer(modifier = Modifier.height(14.dp))
        DonorInfoRow(icon = Icons.Default.Email, label = "Email", value = user.email)
        Spacer(modifier = Modifier.height(14.dp))
        DonorInfoRow(icon = Icons.Default.Phone, label = "Contact", value = user.contactNumber)
        Spacer(modifier = Modifier.height(14.dp))
        DonorInfoRow(icon = Icons.Default.Home, label = "Address", value = user.address)
        Spacer(modifier = Modifier.height(14.dp))
        DonorInfoRow(icon = Icons.Default.Sell, label = "Account Type", value = user.accountType)
    }
}

@Composable
private fun DonorInfoRow(icon: ImageVector, label: String, value: String) {
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
private fun DonorStatsRow(user: UserProfile) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        DonorStatBox(
            value = user.totalRequests.toString(),
            label = "Total Request",
            background = Color(0xFFEDF1FF),
            valueColor = AccentBlue,
            modifier = Modifier.weight(1f)
        )
        DonorStatBox(
            value = user.verifiedRequests.toString(),
            label = "Verified",
            background = Color(0xFFE8F6EE),
            valueColor = Color(0xFF1E8E4E),
            modifier = Modifier.weight(1f)
        )
        DonorStatBox(
            value = user.completedRequests.toString(),
            label = "Completed",
            background = Color(0xFFE8F6EE),
            valueColor = Color(0xFF1E8E4E),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DonorStatBox(
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
private fun DonorMenuCard(
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onAboutBayanihanLink: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
    ) {
        DonorMenuRow(icon = Icons.Default.Edit, label = "Edit Profile", onClick = onEditProfile)
        DonorMenuDivider()
        DonorMenuRow(icon = Icons.Default.Lock, label = "Change Password", onClick = onChangePassword)
        DonorMenuDivider()
        DonorMenuRow(icon = Icons.Default.Info, label = "About BayanihanLink", onClick = onAboutBayanihanLink)
    }
}

@Composable
private fun DonorMenuRow(icon: ImageVector, label: String, onClick: () -> Unit) {
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
private fun DonorMenuDivider() {
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderGray))
}

@Composable
private fun DonorLogOutButton(onClick: () -> Unit) {
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
private fun DonorProfileBottomBar(
    onNeedsClick: () -> Unit,
    onMyOffersClick: () -> Unit,
    onAlertsClick: () -> Unit
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        NavigationBarItem(
            selected = false,
            onClick = onNeedsClick,
            icon = { Icon(Icons.Default.GridView, contentDescription = "Needs") },
            label = { Text("Needs", fontSize = 10.sp) },
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
            onClick = onMyOffersClick,
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "My Offers") },
            label = { Text("My Offers", fontSize = 10.sp) },
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
