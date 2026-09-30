package com.example.bayanihanlink

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AccentBlue = Color(0xFF2B49CC)
private val LabelGray = Color(0xFF8A8FA3)
private val BorderGray = Color(0xFFE3E5EC)

// The 4 filter chips at the top: All | Critical | Urgent | Normal
private enum class NeedFilter { ALL, CRITICAL, URGENT, NORMAL }

private data class CommunityNeed(
    val id: String,
    val urgency: String, // "CRITICAL", "URGENT", "NORMAL"
    val title: String,
    val itemName: String,
    val neededQuantity: String,
    val description: String,
    val location: String,
    val date: String
)

private val sampleNeeds = listOf(
    CommunityNeed(
        id = "#BL-000234",
        urgency = "CRITICAL",
        title = "Typhoon Assistance",
        itemName = "Drinking Water",
        neededQuantity = "Needed: 100 bottles",
        description = "50 families in evacuation center urgently need clean drinking water.",
        location = "Cagayan de Oro",
        date = "Sep 15, 2026"
    ),
    CommunityNeed(
        id = "#BL-000241",
        urgency = "URGENT",
        title = "Flood Assistance",
        itemName = "Rice (5kg sacks)",
        neededQuantity = "Needed: 30 sacks",
        description = "Families displaced by flooding need food supply for at least 1 week.",
        location = "Iligan City, Lanao",
        date = "Sep 15, 2026"
    ),
    CommunityNeed(
        id = "#BL-000256",
        urgency = "URGENT",
        title = "Earthquake Assistance",
        itemName = "Blankets & Clothing",
        neededQuantity = "Needed: 50 sets",
        description = "Families living outside need warmth and clothing.",
        location = "Surigao del Norte",
        date = "Sep 14, 2026"
    ),
    CommunityNeed(
        id = "#BL-000261",
        urgency = "NORMAL",
        title = "Fire Assistance",
        itemName = "Hygiene Supplies",
        neededQuantity = "Needed: 40 packs",
        description = "Fire victims need basic hygiene kits.",
        location = "Davao City",
        date = "Sep 13, 2026"
    ),
    CommunityNeed(
        id = "#BL-000270",
        urgency = "NORMAL",
        title = "Typhoon Assistance",
        itemName = "Medicine (First Aid)",
        neededQuantity = "Needed: 20 kits",
        description = "Medical supplies needed for minor injuries and illnesses.",
        location = "Butuan City",
        date = "Sep 12, 2026"
    )
)

@Composable
fun DonorHomeScreen(
    userName: String = "Guest",
    onViewRequest: (String) -> Unit = {},
    onNavigateMyOffers: () -> Unit = {},
    onNavigateAlerts: () -> Unit = {},
    onNavigateProfile: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(NeedFilter.ALL) }
    var searchText by remember { mutableStateOf("") }

    val filteredNeeds = sampleNeeds
        .filter { need -> selectedFilter == NeedFilter.ALL || need.urgency == selectedFilter.name }
        .filter { need ->
            searchText.isBlank() ||
                    need.title.contains(searchText, ignoreCase = true) ||
                    need.location.contains(searchText, ignoreCase = true)
        }

    Scaffold(
        bottomBar = {
            DonorBottomBar(
                onMyOffersClick = onNavigateMyOffers,
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
            DonorHeader(
                userName = userName,
                searchText = searchText,
                onSearchTextChange = { searchText = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilterChipsRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                filteredNeeds.forEach { need ->
                    CommunityNeedCard(
                        need = need,
                        onViewRequest = { onViewRequest(need.id) }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

// ---------- Top blue header: greeting + avatar + "Community Needs" + search bar ----------
@Composable
private fun DonorHeader(
    userName: String,
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    // Turn "Ronaldo Geronimo" into initials "RG" for the avatar circle.
    val initials = remember(userName) {
        userName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AccentBlue, RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 44.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Good morning,",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = userName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = initials, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Community Needs",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(

                text = "${sampleNeeds.size} verified requests",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // Search bar, overlapping the bottom curve of the header
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            placeholder = { Text("Search needs, location, disaster...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = LabelGray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = BorderGray,
                focusedBorderColor = AccentBlue
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-24).dp)
        )
    }
}

// ---------- Filter chips: All | Critical | Urgent | Normal ----------
@Composable
private fun FilterChipsRow(selectedFilter: NeedFilter, onFilterSelected: (NeedFilter) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        NeedFilter.entries.forEach { filter ->
            val isSelected = filter == selectedFilter
            val chipColor = when (filter) {
                NeedFilter.ALL -> AccentBlue
                NeedFilter.CRITICAL -> Color(0xFFE53935)
                NeedFilter.URGENT -> Color(0xFFF5A623)
                NeedFilter.NORMAL -> Color(0xFF3BB273)
            }
            Box(
                modifier = Modifier
                    .background(
                        color = if (isSelected) chipColor else Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onFilterSelected(filter) }
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = filter.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White else LabelGray
                )
            }
        }
    }
}

// ---------- One community need card ----------
@Composable
private fun CommunityNeedCard(need: CommunityNeed, onViewRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UrgencyTag(need.urgency)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = need.id, fontSize = 12.sp, color = LabelGray)
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(text = need.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = need.itemName, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(text = need.neededQuantity, fontSize = 13.sp, color = AccentBlue, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(text = need.description, fontSize = 13.sp, color = LabelGray)

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = LabelGray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = need.location, fontSize = 12.sp, color = LabelGray)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = need.date, fontSize = 12.sp, color = LabelGray)
            }

            Button(
                onClick = onViewRequest,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("View Request", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun UrgencyTag(urgency: String) {
    val (background, textColor) = when (urgency) {
        "CRITICAL" -> Color(0xFFFAD9DC) to Color(0xFFC62828)
        "URGENT" -> Color(0xFFFCEBD2) to Color(0xFFB4690E)
        else -> Color(0xFFDFF3E3) to Color(0xFF2E7D32) // "NORMAL"
    }
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = urgency, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

// ---------- Bottom navigation bar (Donor/Volunteer version) ----------
@Composable
private fun DonorBottomBar(
    onMyOffersClick: () -> Unit,
    onAlertsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = true,
            onClick = { /* already on this screen */ },
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