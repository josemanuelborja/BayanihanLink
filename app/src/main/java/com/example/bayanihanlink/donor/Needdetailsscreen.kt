package com.example.bayanihanlink.donor

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.foundation.layout.aspectRatio
import com.example.bayanihanlink.api.RequestResponse
import com.example.bayanihanlink.api.RetrofitClient
import com.example.bayanihanlink.api.readableErrorMessage
import com.example.bayanihanlink.ui.components.RemoteImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.style.TextAlign
import com.example.bayanihanlink.ui.theme.AppColors

private val AccentBlue: Color
    @Composable get() = AppColors.accentBlue
private val LabelGray: Color
    @Composable get() = AppColors.labelGray
private val BorderGray: Color
    @Composable get() = AppColors.borderGray
private val TextPrimary: Color
    @Composable get() = AppColors.textPrimary
private val DoneGreen = Color(0xFF3BB273)
private val CriticalRed = Color(0xFFC62828)
private val OfferOrange = Color(0xFFE65100)

data class NeedDetail(
    val id: String,
    val title: String,
    val date: String,
    val urgency: String,
    val isVerified: Boolean,
    val disasterType: String,
    val category: String,
    val location: String,
    val itemName: String,
    val neededQuantityLabel: String,
    val description: String,
    val quantityOffered: Int,
    val quantityTarget: Int,
    val evidenceImageUrl: String = ""
)

// Turns the data we got from the backend into the shape this screen uses.
fun RequestResponse.toNeedDetail(): NeedDetail = NeedDetail(
    id = id,
    title = disasterType ?: "Assistance Request",
    date = dateSubmitted?.take(10) ?: "-",
    urgency = urgency ?: "NORMAL",
    isVerified = status == "VERIFIED" || status == "ASSISTED" || status == "COMPLETED",
    disasterType = disasterType ?: "-",
    category = categories?.joinToString(", ").takeUnless { it.isNullOrBlank() } ?: category ?: "-",
    location = location ?: "-",
    itemName = itemNeeded ?: "-",
    neededQuantityLabel = "${quantity ?: ""} ${unit ?: ""}".trim().ifBlank { "-" },
    description = description ?: "",
    quantityOffered = 0,
    quantityTarget = quantity?.toIntOrNull() ?: 0,
    evidenceImageUrl = if (evidenceImage.isNullOrBlank()) "" else RetrofitClient.getImageUrl(evidenceImage)
)

@Composable
fun NeedDetailsScreen(
    needId: String,
    detail: NeedDetail? = null,
    onBack: () -> Unit = {},
    onOfferAssistance: () -> Unit = {}
) {
    // If the caller did not pass the data, load this exact request from the backend.
    var loadedDetail by remember(needId) { mutableStateOf(detail) }
    var isLoading by remember(needId) { mutableStateOf(detail == null) }
    var errorMessage by remember(needId) { mutableStateOf<String?>(null) }
    var reloadKey by remember(needId) { mutableStateOf(0) }

    LaunchedEffect(needId, reloadKey) {
        if (loadedDetail == null) {
            isLoading = true
            errorMessage = null
            try {
                loadedDetail = RetrofitClient.api.getRequestById(needId).toNeedDetail()
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
            Spacer(modifier = Modifier.height(60.dp))

            val detailData = loadedDetail
            if (detailData == null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 40.dp)
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
                            textAlign = TextAlign.Center
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
                // Urgency-colored header card
                val headerColor = when (detailData.urgency.uppercase()) {
                    "CRITICAL" -> CriticalRed
                    "URGENT" -> OfferOrange
                    else -> DoneGreen
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .background(headerColor, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(detailData.urgency.uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        if (detailData.isVerified) {
                            Text("● Verified", color = Color.White, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(detailData.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${detailData.id} · ${detailData.date}", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    if (detailData.evidenceImageUrl.isNotBlank()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Text("Image of Disaster", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(12.dp))
                            RemoteImage(
                                imageUrl = detailData.evidenceImageUrl,
                                contentDescription = "Evidence photo for ${detailData.id}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Disaster Details card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = 1.dp, color = BorderGray, shape = RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text("Disaster Details", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(12.dp))

                        LabeledRow(label = "Disaster Type", value = detailData.disasterType)
                        Spacer(modifier = Modifier.height(8.dp))
                        LabeledRow(label = "Category", value = detailData.category)
                        Spacer(modifier = Modifier.height(8.dp))
                        LabeledRow(label = "Location", value = detailData.location)

                        if (detailData.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LabeledRow(label = "Description", value = detailData.description)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Requested Items card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = 1.dp, color = BorderGray, shape = RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text("Requested Items", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Text(text = detailData.itemName, fontSize = 14.sp, color = TextPrimary)
                            if (detailData.neededQuantityLabel.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .background(AppColors.accentSoft, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(text = detailData.neededQuantityLabel, fontSize = 12.sp, color = AccentBlue, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onOfferAssistance,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Offer Assistance", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(30.dp))
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
                    Icon(Icons.Default.ArrowBack, contentDescription = "Go back", tint = TextPrimary)
                }
                Text("Request Details", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
        }
    }
}

@Composable
private fun LabeledRow(label: String, value: String) {
    Row {
        Text(text = label, fontSize = 12.sp, color = LabelGray, modifier = Modifier.weight(0.35f))
        Text(text = value, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(0.65f))
    }
}
