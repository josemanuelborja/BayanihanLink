package com.example.bayanihanlink

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
private val DoneGreen = Color(0xFF3BB273)

// One line item inside "Requested Items" (e.g. "Rice (5kg sacks)" + "10 packs").
data class RequestedItem(val name: String, val quantityLabel: String)

// The 6 steps of a request's life cycle, shown in the vertical timeline.
private val timelineSteps = listOf(
    "Submitted",
    "Pending Verification",
    "Verified",
    "Assistance Offered",
    "Assisted",
    "Completed"
)

// Everything shown on this screen for one request.
data class RequestDetail(
    val id: String,
    val urgency: String,        // "URGENT", "CRITICAL", "NORMAL"
    val statusLabel: String,    // "Verified", "Pending", "Assisted", "Completed"
    val disasterType: String,
    val category: String,
    val dateSubmitted: String,
    val location: String,
    val items: List<RequestedItem>,
    val personsAffected: Int,
    // Which step (1-6, matching "timelineSteps" above) is the CURRENT one.
    // Steps before this are shown as done (green check); steps after are upcoming (gray).
    val currentTimelineStep: Int
)

// Sample data so this screen works right away. In a real app, you'd look up
// the real request using the "requestId" passed into this screen (for example,
// from your database) instead of always returning this same sample.
private fun sampleRequestDetail(requestId: String): RequestDetail = RequestDetail(
    id = requestId,
    urgency = "URGENT",
    statusLabel = "Verified",
    disasterType = "Typhoon",
    category = "Food & Water",
    dateSubmitted = "Sep 15, 2026",
    location = "Brgy. 14, CDO",
    items = listOf(
        RequestedItem("Rice (5kg sacks)", "10 packs"),
        RequestedItem("Drinking Water (1L)", "50 bottles")
    ),
    personsAffected = 5,
    currentTimelineStep = 3
)

@Composable
fun RequestDetailsScreen(
    requestId: String,
    onBack: () -> Unit = {}
) {
    val detail = sampleRequestDetail(requestId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar: back arrow + title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Go back", tint = Color(0xFF1A1A2E))
            }
            Text("Request Details", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SummaryCard(detail = detail)
            Spacer(modifier = Modifier.height(16.dp))
            RequestedItemsCard(detail = detail)
            Spacer(modifier = Modifier.height(16.dp))
            StatusTimelineCard(currentStep = detail.currentTimelineStep)
            Spacer(modifier = Modifier.height(16.dp))
            GovernmentCoordinationCard()
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ---------- Card 1: Request ID + tags + Disaster/Category/Date/Location ----------
@Composable
private fun SummaryCard(detail: RequestDetail) {
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
                Text("Request ID", fontSize = 12.sp, color = LabelGray)
                Text(detail.id, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AccentBlue)
            }
            Column(horizontalAlignment = Alignment.End) {
                UrgencyTag(detail.urgency)
                Spacer(modifier = Modifier.height(6.dp))
                StatusTag(detail.statusLabel)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F4FF), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                DetailPair(label = "Disaster", value = detail.disasterType, modifier = Modifier.weight(1f))
                DetailPair(label = "Category", value = detail.category, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                DetailPair(label = "Date Submitted", value = detail.dateSubmitted, modifier = Modifier.weight(1f))
                DetailPair(label = "Location", value = detail.location, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DetailPair(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 12.sp, color = LabelGray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A2E))
    }
}

@Composable
private fun UrgencyTag(urgency: String) {
    val (background, textColor) = when (urgency) {
        "URGENT" -> Color(0xFFFCEBD2) to Color(0xFFB4690E)
        "CRITICAL" -> Color(0xFFFAD9DC) to Color(0xFFC62828)
        else -> Color(0xFFDFF3E3) to Color(0xFF2E7D32)
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
private fun StatusTag(status: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFE3E9FF), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = status, color = AccentBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

// ---------- Card 2: "Requested Items" ----------
@Composable
private fun RequestedItemsCard(detail: RequestDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderGray, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("Requested Items", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        detail.items.forEach { item ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(text = item.name, fontSize = 14.sp, color = Color(0xFF1A1A2E))
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE3E9FF), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = item.quantityLabel, fontSize = 12.sp, color = AccentBlue, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderGray))
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Persons affected: ${detail.personsAffected} family members",
            fontSize = 13.sp,
            color = LabelGray
        )
    }
}

// ---------- Card 3: "Status Timeline" (vertical version of the step tracker) ----------
@Composable
private fun StatusTimelineCard(currentStep: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderGray, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("Status Timeline", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(14.dp))

        timelineSteps.forEachIndexed { index, label ->
            val stepNumber = index + 1
            val isDone = stepNumber < currentStep
            val isCurrent = stepNumber == currentStep
            val circleColor = when {
                isDone -> DoneGreen
                isCurrent -> AccentBlue
                else -> Color(0xFFE3E5EC)
            }
            val textColor = when {
                isDone -> DoneGreen
                isCurrent -> AccentBlue
                else -> LabelGray
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
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
                                text = stepNumber.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    // Connecting vertical line (skip after the very last step)
                    if (stepNumber != timelineSteps.size) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(28.dp)
                                .background(if (isDone) DoneGreen else Color(0xFFE3E5EC))
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = textColor,
                    modifier = Modifier.padding(bottom = if (stepNumber != timelineSteps.size) 28.dp else 0.dp)
                )
            }
        }
    }
}

// ---------- Card 4: "Government Coordination" note ----------
@Composable
private fun GovernmentCoordinationCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderGray, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = null,
            tint = Color(0xFF1A1A2E),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("Government Coordination", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Official verification and physical distribution of assistance are handled by the Department of Social Welfare and Development (DSWD) and appropriate government authorities.",
                fontSize = 13.sp,
                color = AccentBlue
            )
        }
    }
}