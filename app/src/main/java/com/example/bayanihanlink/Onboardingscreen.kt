package com.example.bayanihanlink

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue

private val AccentBlue = Color(0xFF2B3FC7)
private val IconCircleBg = Color(0xFFD9DEF7)
private val SubtitleGray = Color(0xFF9598A6)

private data class OnboardingPageData(
    val iconRes: Int,
    val title: String,
    val description: String
)

private val onboardingPages = listOf(
    OnboardingPageData(
        iconRes = R.drawable.message_chatbot,
        title = "Request Assistance",
        description = "When disaster strikes, quickly submit a request for the supplies and help your family needs. Our platform connects you with verified donors."
    ),
    OnboardingPageData(
        iconRes = R.drawable.circle_dashed_check,
        title = "Find Verified Needs",
        description = "Browse the Community Needs Board to see verified assistance requests. Every listing is reviewed by administrators before being published."
    ),
    OnboardingPageData(
        iconRes = R.drawable.heart_discount,
        title = "Offer Assistance",
        description = "Donors and volunteers can offer in-kind assistance directly to verified needs. Help is organized, visible, and coordinated through the system."
    )
)

@Composable
fun OnboardingScreen(
    onDone: () -> Unit,
    onLogIn: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE7ECFC), Color.White),
                    endY = 900f
                )
            )
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardingPageContent(onboardingPages[page])
            }

            // Dot indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 24.dp)
            ) {
                repeat(onboardingPages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val dotWidth by animateDpAsState(
                        targetValue = if (isSelected) 22.dp else 7.dp,
                        animationSpec = tween(300),
                        label = "dotWidth"
                    )
                    Box(
                        modifier = Modifier
                            .height(7.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(if (isSelected) AccentBlue else Color(0xFFD6D9E6))
                    )
                }
            }

            // Next / Get Started button
            Button(
                onClick = {
                    if (pagerState.currentPage == onboardingPages.lastIndex) {
                        onDone()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text(
                    text = if (pagerState.currentPage == onboardingPages.lastIndex) "Get Started" else "Next",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val loginText = buildAnnotatedString {
                withStyle(SpanStyle(color = SubtitleGray)) {
                    append("Already have an account? ")
                }
                withStyle(SpanStyle(color = AccentBlue, fontWeight = FontWeight.SemiBold)) {
                    append("Log in")
                }
            }
            Text(
                text = loginText,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onLogIn
                    )
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(pageData: OnboardingPageData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(IconCircleBg),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = pageData.iconRes),
                contentDescription = pageData.title,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = pageData.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A2E),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = pageData.description,
            fontSize = 14.sp,
            color = SubtitleGray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}