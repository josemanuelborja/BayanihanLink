package com.example.bayanihanlink.individual

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bayanihanlink.api.RequestResponse
import com.example.bayanihanlink.api.RetrofitClient
import com.example.bayanihanlink.api.readableErrorMessage
import com.example.bayanihanlink.ui.components.RemoteImage
import com.example.bayanihanlink.ui.theme.AppColors

private val AccentBlue: Color
    @Composable @ReadOnlyComposable get() = AppColors.accentBlue
private val LabelGray: Color
    @Composable @ReadOnlyComposable get() = AppColors.labelGray
private val BorderGray: Color
    @Composable @ReadOnlyComposable get() = AppColors.borderGray
private val TextPrimary: Color
    @Composable @ReadOnlyComposable get() = AppColors.textPrimary
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
    val currentTimelineStep: Int,
    // Full address of the uploaded evidence photo ("" when there is none).
    val evidenceImageUrl: String = ""
)

// Turns the data we got from the backend into the shape this screen uses.
fun RequestResponse.toRequestDetail(): RequestDetail {
    val status = status ?: "PENDING"
    return RequestDetail(
        id = id,
        urgency = urgency ?: "NORMAL",
        statusLabel = status.lowercase().replaceFirstChar { it.uppercase() },
        disasterType = disasterType ?: "-",
        category = categories?.joinToString(", ")
            .takeUnless { it.isNullOrBlank() }
            ?: category
            ?: "-",
        dateSubmitted = dateSubmitted?.take(10) ?: "-",
        location = location ?: "-",
        items = listOf(
            RequestedItem(
                name = itemNeeded ?: "-",
                quantityLabel = "${quantity ?: ""} ${unit ?: ""}".trim()
            )
        ),
        personsAffected = personsAffected ?: 0,
        currentTimelineStep = currentTimelineStep ?: when (status) {
            "VERIFIED" -> 3
            "ASSISTED" -> 5
            "COMPLETED" -> 6
            else -> 2
        },
        evidenceImageUrl = if (evidenceImage.isNullOrBlank()) "" else RetrofitClient.getImageUrl(evidenceImage)
    )
}

@Composable
fun RequestDetailsScreen(
    requestId: String,
    detail: RequestDetail? = null,
    onBack: () -> Unit = {}
) {
    // If the caller did not pass the request data, we load it from the backend.
    var loadedDetail by remember(requestId) { mutableStateOf(detail) }
    var isLoading by remember(requestId) { mutableStateOf(detail == null) }
    var errorMessage by remember(requestId) { mutableStateOf<String?>(null) }
    var reloadKey by remember(requestId) { mutableStateOf(0) }

    LaunchedEffect(requestId, reloadKey) {
        if (loadedDetail == null) {
            isLoading = true
            errorMessage = null
            try {
                loadedDetail = RetrofitClient.api.getRequestById(requestId).toRequestDetail()
            } catch (error: Exception) {
                errorMessage = readableErrorMessage(error)
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Spacer to push content down below the sticky header height
            Spacer(modifier = Modifier.height(60.dp))

            val detailData = loadedDetail
            if (detailData == null) {
                // Loading / error state
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 48.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = AccentBlue, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading request…", fontSize = 13.sp, color = LabelGray)
                    } else {
                        Text(
                            text = errorMessage ?: "Could not load this request.",
                            fontSize = 13.sp,
                            color = LabelGray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { reloadKey += 1 },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                        ) {
                            Text("Try Again", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SummaryCard(detail = detailData)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Image of Disaster card — shows the photo sent with the request
                    ImageOfDisasterCard(imageUrl = detailData.evidenceImageUrl)
                    Spacer(modifier = Modifier.height(16.dp))

                    RequestedItemsCard(detail = detailData)
                    Spacer(modifier = Modifier.height(16.dp))
                    StatusTimelineCard(currentStep = detailData.currentTimelineStep)
                    Spacer(modifier = Modifier.height(16.dp))
                    GovernmentCoordinationCard()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Sticky Header at the top
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            color = AppColors.background,
            shadowElevation = 2.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 8.dp, bottom = 8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back", tint = TextPrimary)
                }
                Text("Request Details", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
        }
    }
}

// ---------- Image of Disaster card ----------
@Composable
private fun ImageOfDisasterCard(imageUrl: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderGray, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("Image of Disaster", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        RemoteImage(
            imageUrl = imageUrl,
            contentDescription = "Evidence photo for this request",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            emptyText = "No image attached"
        )
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("Request ID", fontSize = 12.sp, color = LabelGray)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    detail.id,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UrgencyTag(detail.urgency)
                StatusTag(detail.statusLabel)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.accentSoft, RoundedCornerShape(12.dp))
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
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

@Composable
private fun UrgencyTag(urgency: String) {
    val (background, textColor) = when (urgency.uppercase()) {
        "URGENT" -> Color(0xFFFCEBD2) to Color(0xFFB4690E)
        "CRITICAL" -> Color(0xFFFAD9DC) to Color(0xFFC62828)
        else -> Color(0xFFDFF3E3) to Color(0xFF2E7D32)
    }
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = urgency.uppercase(), color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StatusTag(status: String) {
    Box(
        modifier = Modifier
            .background(AppColors.accentSoft, RoundedCornerShape(8.dp))
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
        Text("Requested Items", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        detail.items.forEach { item ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(text = item.name, fontSize = 14.sp, color = TextPrimary)
                if (item.quantityLabel.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .background(AppColors.accentSoft, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = item.quantityLabel, fontSize = 12.sp, color = AccentBlue, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderGray))
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Persons affected: ${detail.personsAffected}",
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
        Text("Status Timeline", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(14.dp))

        timelineSteps.forEachIndexed { index, label ->
            val stepNumber = index + 1
            val isDone = stepNumber < currentStep
            val isCurrent = stepNumber == currentStep
            val circleColor = when {
                isDone -> DoneGreen
                isCurrent -> AccentBlue
                else -> BorderGray
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
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stepNumber.toString(),
                                    color = if (isCurrent && AppColors.isDark) Color(0xFF101018) else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                    // Connecting vertical line (skip after the very last step)
                    if (stepNumber != timelineSteps.size) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(28.dp)
                                .background(if (isDone) DoneGreen else BorderGray)
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
            tint = TextPrimary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("Government Coordination", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Official verification and physical distribution of assistance are handled by the Department of Social Welfare and Development (DSWD) and appropriate government authorities.",
                fontSize = 13.sp,
                color = AccentBlue
            )
        }
    }
}
