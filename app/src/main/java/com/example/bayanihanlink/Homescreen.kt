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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Same colors used across the whole app, so everything matches.
private val AccentBlue = Color(0xFF2B49CC)
private val AlertOrange = Color(0xFFF5A623)
private val UrgentPink = Color(0xFFFCE4EC)
private val UrgentText = Color(0xFFD81B60)
private val VerifiedBlueBg = Color(0xFFE3E9FF)
private val VerifiedBlueText = Color(0xFF2B49CC)
private val CardGray = Color(0xFFF7F8FA)
private val LabelGray = Color(0xFF8A8FA3)

// One "step" in the request's progress (Submitted -> Verified -> Offered -> Assisted).
private data class RequestStep(val label: String, val stepNumber: Int)

private val requestSteps = listOf(
    RequestStep("Submitted", 1),
    RequestStep("Verified", 2),
    RequestStep("Offered", 3),
    RequestStep("Assisted", 4)
)

// One row in the "Recent Updates" list.
private data class UpdateItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconBackground: Color,
    val message: String,
    val timeAgo: String
)

@Composable
fun HomeScreen(
    userName: String = "Maria Santos",
    onRequestAssistance: () -> Unit = {},
    onViewRequestDetails: () -> Unit = {},
    onNavigateMyRequest: () -> Unit = {},
    onNavigateAlerts: () -> Unit = {},
    onNavigateProfile: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            HomeBottomBar(
                onMyRequestClick = onNavigateMyRequest,
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
            HomeHeader(userName = userName)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp)
                    .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(top = 20.dp)
            ) {
                RequestAssistanceCard(onClick = onRequestAssistance)

                Spacer(modifier = Modifier.height(20.dp))

                ActiveRequestSection(onViewDetails = onViewRequestDetails)

                Spacer(modifier = Modifier.height(20.dp))

                OverviewSection()

                Spacer(modifier = Modifier.height(20.dp))

                RecentUpdatesSection()

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ---------- Top blue header: greeting + avatar + typhoon alert banner ----------
@Composable
private fun HomeHeader(userName: String) {
    // Turn "Maria Santos" into initials "MS" for the little avatar circle.
    val initials = remember(userName) {
        userName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AccentBlue
            )
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 44.dp) // extra bottom room for the white sheet to overlap into
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
            // Avatar circle with initials
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

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(AlertOrange, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Bolt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Typhoon Carina - Active",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Relief operations ongoing in your area",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ---------- "Need help? Submit a request now." card ----------
@Composable
private fun RequestAssistanceCard(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(CardGray, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Need help? Submit a request now.",
            fontSize = 13.sp,
            color = LabelGray
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text("+ Request Assistance", fontWeight = FontWeight.SemiBold)
        }
    }
}

// ---------- "My Active Request" card with the step tracker ----------
@Composable
private fun ActiveRequestSection(onViewDetails: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("My Active Request", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("#BL-000234", fontSize = 12.sp, color = LabelGray)
                    Text("Typhoon Assistance", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Food & Drinking Water", fontSize = 13.sp, color = LabelGray)
                }
                Column(horizontalAlignment = Alignment.End) {
                    StatusTag(text = "URGENT", background = UrgentPink, textColor = UrgentText)
                    Spacer(modifier = Modifier.height(6.dp))
                    StatusTag(text = "Verified", background = VerifiedBlueBg, textColor = VerifiedBlueText)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            StepTracker(currentStep = 3)

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "View Details \u2192",
                color = AccentBlue,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.clickableSimple(onViewDetails)
            )
        }
    }
}

@Composable
private fun StatusTag(text: String, background: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = text, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

// Draws the 4 circles connected by lines: green check = done, blue number = current, gray = upcoming.
@Composable
private fun StepTracker(currentStep: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        requestSteps.forEachIndexed { index, step ->
            val isDone = step.stepNumber < currentStep
            val isCurrent = step.stepNumber == currentStep
            val circleColor = when {
                isDone -> Color(0xFF3BB273)      // green
                isCurrent -> AccentBlue           // blue
                else -> Color(0xFFE3E5EC)         // gray
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    // Line before the circle (skip for the very first step)
                    if (index != 0) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (isDone || isCurrent) Color(0xFF3BB273) else Color(0xFFE3E5EC))
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(circleColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = step.stepNumber.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Line after the circle (skip for the very last step)
                    if (index != requestSteps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (isDone) Color(0xFF3BB273) else Color(0xFFE3E5EC))
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.label,
                    fontSize = 10.sp,
                    color = if (isDone) Color(0xFF3BB273) else if (isCurrent) AccentBlue else LabelGray
                )
            }
        }
    }
}

// ---------- "Overview" section: 3 stat boxes ----------
@Composable
private fun OverviewSection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("Overview", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatBox(value = "2", label = "Total Request", valueColor = Color(0xFF1A1A2E), modifier = Modifier.weight(1f))
            StatBox(value = "1", label = "Verified", valueColor = Color(0xFF3BB273), modifier = Modifier.weight(1f))
            StatBox(value = "0", label = "Completed", valueColor = AccentBlue, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatBox(value: String, label: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(CardGray, RoundedCornerShape(14.dp))
            .padding(vertical = 16.dp)
    ) {
        Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = valueColor)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = LabelGray)
    }
}

// ---------- "Recent Updates" section ----------
@Composable
private fun RecentUpdatesSection() {
    val updates = listOf(
        UpdateItem(
            icon = Icons.Default.CheckCircle,
            iconBackground = Color(0xFF3BB273),
            message = "Your request #BL-000234 has been verified by the administrator.",
            timeAgo = "2 hours ago"
        ),
        UpdateItem(
            icon = Icons.Default.Favorite,
            iconBackground = AlertOrange,
            message = "A donor has offered assistance for your request.",
            timeAgo = "5 hours ago"
        ),
        UpdateItem(
            icon = Icons.Default.Star,
            iconBackground = Color(0xFF3BB273),
            message = "Your request #BL-000210 has been completed.",
            timeAgo = "3 days ago"
        )
    )

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("Recent Updates", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        updates.forEach { update ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .background(Color.White, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(update.iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = update.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = update.message, fontSize = 13.sp, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = update.timeAgo, fontSize = 11.sp, color = LabelGray)
                }
            }
        }
    }
}
@Composable
private fun HomeBottomBar(
    onMyRequestClick: () -> Unit,
    onAlertsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = true,
            onClick = { /* already on this screen */ },
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
            selected = false,
            onClick = onProfileClick,
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentBlue, selectedTextColor = AccentBlue)
        )
    }
}

// Small helper so text links (like "View Details ->") don't show a ripple box behind them.
@Composable
private fun Modifier.clickableSimple(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onClick = onClick
)