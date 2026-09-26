package com.example.bayanihanlink

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AccentBlue = Color(0xFF2B49CC)
private val LabelGray = Color(0xFF8A8FA3)
private val BorderGray = Color(0xFFE3E5EC)
enum class RequestStatus { PENDING, VERIFIED, ASSISTED, COMPLETED }
enum class RequestFilter { ALL, PENDING, VERIFIED, ASSISTED, COMPLETED }
data class RequestItem(
    val id: String,
    val title: String,
    val items: String,
    val quantity: String,
    val date: String,
    val urgency: String, // "URGENT", "CRITICAL", or "NORMAL"
    val status: RequestStatus,
    val recencyOrder: Int
)
private val sampleRequests = listOf(
    RequestItem(
        id = "#BL-000234",
        title = "Typhoon Assistance",
        items = "Food, Drinking Water",
        quantity = "10 packs, 50 bottles",
        date = "Sep 15, 2026",
        urgency = "URGENT",
        status = RequestStatus.VERIFIED,
        recencyOrder = 3
    ),
    RequestItem(
        id = "#BL-000210",
        title = "Flood Assistance",
        items = "Clothing, Blankets",
        quantity = "20 sets, 10 pieces",
        date = "Sep 8, 2026",
        urgency = "NORMAL",
        status = RequestStatus.COMPLETED,
        recencyOrder = 2
    ),
    RequestItem(
        id = "#BL-00189",
        title = "Typhoon Assistance",
        items = "Medicine",
        quantity = "5 packs",
        date = "Sep 3, 2026",
        urgency = "CRITICAL",
        status = RequestStatus.ASSISTED,
        recencyOrder = 1
    )
)

@Composable
fun MyRequestsScreen(
    onViewDetails: (RequestItem) -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateAlerts: () -> Unit = {},
    onNavigateProfile: () -> Unit = {}
) {

    var selectedFilter by remember { mutableStateOf(RequestFilter.ALL) }

    val filteredRequests = sampleRequests.filter { request ->
        selectedFilter == RequestFilter.ALL || request.status.name == selectedFilter.name
    }
        .sortedByDescending { it.recencyOrder }

    Scaffold(
        bottomBar = {
            MyRequestsBottomBar(
                onHomeClick = onNavigateHome,
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
        ) {
            Text(
                text = "My Requests",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp)
            )

            FilterTabsRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredRequests.isEmpty()) {
                EmptyState(selectedFilter = selectedFilter)
            } else {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    filteredRequests.forEach { request ->
                        RequestCard(
                            request = request,
                            onViewDetails = { onViewDetails(request) }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

// The horizontally-scrollable row of filter: All | Pending | Verified | Assisted | Completed
@Composable
private fun FilterTabsRow(
    selectedFilter: RequestFilter,
    onFilterSelected: (RequestFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RequestFilter.entries.forEach { filter ->
            FilterChip(
                label = filterLabel(filter),
                isSelected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

// Turns "PENDING" into "Pending", "ALL" into "All", etc. for display.
private fun filterLabel(filter: RequestFilter): String =
    filter.name.lowercase().replaceFirstChar { it.uppercase() }

@Composable
private fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) AccentBlue else Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) AccentBlue else BorderGray,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else LabelGray
        )
    }
}

// Shown when the selected filter has zero matching requests.
@Composable
private fun EmptyState(selectedFilter: RequestFilter) {
    val message = if (selectedFilter == RequestFilter.ALL) {
        "No requests yet."
    } else {
        "No ${filterLabel(selectedFilter).lowercase()} requests yet."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.clipboard_smile),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No requests found",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A2E)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = message, fontSize = 13.sp, color = LabelGray)
    }
}

@Composable
private fun RequestCard(request: RequestItem, onViewDetails: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderGray, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(text = request.id, fontSize = 12.sp, color = LabelGray)
                Text(text = request.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                UrgencyTag(urgency = request.urgency)
                Spacer(modifier = Modifier.height(6.dp))
                StatusTag(status = request.status)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        LabeledRow(label = "Items", value = request.items)
        Spacer(modifier = Modifier.height(4.dp))
        LabeledRow(label = "Qty", value = request.quantity)
        Spacer(modifier = Modifier.height(4.dp))
        LabeledRow(label = "Date", value = request.date)

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "View Details \u2192",
            color = AccentBlue,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onViewDetails
            )
        )
    }
}

@Composable
private fun LabeledRow(label: String, value: String) {
    Row {
        Text(text = label, fontSize = 12.sp, color = LabelGray, modifier = Modifier.weight(0.35f))
        Text(text = value, fontSize = 13.sp, color = Color(0xFF1A1A2E), modifier = Modifier.weight(0.65f))
    }
}

@Composable
private fun UrgencyTag(urgency: String) {
    // Pick a background/text color depending on how urgent the request is.
    val (background, textColor) = when (urgency) {
        "URGENT" -> Color(0xFFFCEBD2) to Color(0xFFB4690E)
        "CRITICAL" -> Color(0xFFFAD9DC) to Color(0xFFC62828)
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

@Composable
private fun StatusTag(status: RequestStatus) {
    // Pick a background/text color depending on the request's current status.
    val (background, textColor, label) = when (status) {
        RequestStatus.PENDING -> Triple(Color(0xFFEDEEF3), LabelGray, "Pending")
        RequestStatus.VERIFIED -> Triple(Color(0xFFE3E9FF), Color(0xFF2B49CC), "Verified")
        RequestStatus.ASSISTED -> Triple(Color(0xFFDFF3E3), Color(0xFF2E7D32), "Assisted")
        RequestStatus.COMPLETED -> Triple(Color(0xFFDDE3FF), Color(0xFF1A2A80), "Completed")
    }
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = label, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
@Composable
private fun MyRequestsBottomBar(
    onHomeClick: () -> Unit,
    onAlertsClick: () -> Unit,
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
            selected = true,
            onClick = { /* already on this screen */ },
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
            selected = false,
            onClick = onProfileClick,
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentBlue, selectedTextColor = AccentBlue)
        )
    }
}