package com.example.bayanihanlink.donor

import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.ReadOnlyComposable
import com.example.bayanihanlink.ui.components.RemoteImage
import com.example.bayanihanlink.ui.theme.AppColors

private val AccentBlue: Color
    @Composable @ReadOnlyComposable get() = AppColors.accentBlue
private val LabelGray: Color
    @Composable @ReadOnlyComposable get() = AppColors.labelGray
private val BorderGray: Color
    @Composable @ReadOnlyComposable get() = AppColors.borderGray

// The 4 filter chips at the top: All | Critical | Urgent | Normal
private enum class NeedFilter { ALL, CRITICAL, URGENT, NORMAL }

// One community need card. Prepared for backend integration. 
data class CommunityNeed( 
    val id: String, 
    val urgency: String, // "CRITICAL", "URGENT", "NORMAL" 
    val title: String, 
    val itemName: String, 
    val neededQuantity: String, 
    val description: String, 
    val location: String, 
    val date: String, 
    val evidenceImageUrl: String = "" 
)

// Returns "Good morning", "Good afternoon", or "Good evening" based on the current time.
// Updates automatically every minute so the greeting stays correct without a page refresh.
private fun timeBasedGreeting(): String {
    val hour = java.time.LocalDateTime.now().hour
    return when {
        hour >= 0 && hour < 12 -> "Good morning"
        hour >= 12 && hour < 18 -> "Good afternoon"
        else -> "Good evening"
    }
}

@Composable
fun DonorHomeScreen(
    userName: String = "Guest",
    needs: List<CommunityNeed> = emptyList(),
    onViewRequest: (String) -> Unit = {},
    onNavigateMyOffers: () -> Unit = {},
    onNavigateAlerts: () -> Unit = {},
    onNavigateProfile: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(NeedFilter.ALL) }
    var searchText by remember { mutableStateOf("") }

    // Recompute the greeting every minute so it stays accurate without refreshing.
    var greeting by remember { mutableStateOf(timeBasedGreeting()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60000) // check every minute
            greeting = timeBasedGreeting()
        }
    }

    val needsList = needs
    val filteredNeeds = needsList
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
                .background(AppColors.background)
                .verticalScroll(rememberScrollState())
        ) {
            DonorHeader(
                greeting = greeting,
                userName = userName,
                needsCount = needsList.size,
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
                if (filteredNeeds.isEmpty()) {
                    // Nothing to show: either there are no requests yet, or the
                    // search/filter matched nothing.
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp, bottom = 24.dp)
                    ) {
                        Text(
                            text = if (needsList.isEmpty()) "No community needs yet." else "No needs match your search.",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (needsList.isEmpty()) {
                                "Requests from citizens will appear here once they are verified."
                            } else {
                                "Try a different keyword or filter."
                            },
                            fontSize = 13.sp,
                            color = LabelGray
                        )
                    }
                }

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
    greeting: String,
    userName: String,
    needsCount: Int = 0,
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
                .background(AppColors.headerBlue, RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
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
                        text = greeting + ",",
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
                text = "$needsCount verified requests",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // Search bar, overlapping the bottom curve of the header (same trick
        // used on the other Home screen: shift up by a fixed amount).
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            placeholder = { Text("Search needs, location, disaster...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = LabelGray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = AppColors.surface,
                focusedContainerColor = AppColors.surface,
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
                        color = if (isSelected) chipColor else AppColors.surface,
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
            // The WHOLE card is clickable (not just the button below), with the
            // normal ripple touch feedback clipped to the card's shape.
            .clip(RoundedCornerShape(16.dp))
            .clickable { onViewRequest() }
            // A border is needed here — without it, a white card on a white
            // page background is invisible (no edges to see where it starts/ends).
            .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
            .background(AppColors.surface, RoundedCornerShape(16.dp))
    ) {
        // Disaster image at the top of the card (shows "No image attached" when
        // this particular request has no photo).
        if (need.evidenceImageUrl.isNotBlank()) {
            RemoteImage(
                imageUrl = need.evidenceImageUrl,
                contentDescription = "Evidence photo for ${need.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                emptyText = "No image attached"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UrgencyTag(need.urgency)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = need.id, fontSize = 12.sp, color = LabelGray)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = need.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = need.itemName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AppColors.textPrimary)
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

                // This button opens the same screen as tapping the card.
                // The button handles its own tap, so the card does not also
                // open it twice.
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
    NavigationBar(containerColor = AppColors.surface) {
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