package com.example.bayanihanlink

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val SplashBlue = Color(0xFF2B49CC)

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    splashDurationMillis: Long = 2400L
) {
    // Logo pop-in animation
    var logoVisible by remember { mutableFloatStateOf(0f) }
    val logoScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = logoVisible,
        animationSpec = tween(durationMillis = 600, easing = EaseOutBack),
        label = "logoScale"
    )
    val logoAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = logoVisible,
        animationSpec = tween(durationMillis = 500),
        label = "logoAlpha"
    )

    // Text fade-in, slightly delayed after the logo
    var textVisible by remember { mutableFloatStateOf(0f) }
    val textAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = textVisible,
        animationSpec = tween(durationMillis = 500),
        label = "textAlpha"
    )

    // Pulsing "Loading..." dots
    val infiniteTransition = rememberInfiniteTransition(label = "loadingDots")

    LaunchedEffect(Unit) {
        logoVisible = 1f
        delay(150)
        textVisible = 1f
        delay(splashDurationMillis)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBlue)
    ) {
        // Decorative background circles
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )
        Box(
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.CenterStart)
                .offset(x = (-40).dp, y = (-40).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-90).dp, y = 90.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )

        // Center content: logo mark, app name, tagline
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "BayanihanLink logo",
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "BayanihanLink",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Connecting Needs with Helping Hands",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                modifier = Modifier.alpha(textAlpha)
            )
        }

        // Bottom loading indicator + footer text
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(3) { index ->
                    val dotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.25f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 600, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse,
                            initialStartOffset = androidx.compose.animation.core.StartOffset(
                                index * 150
                            )
                        ),
                        label = "dot$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = dotAlpha))
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Loading...",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Department of Social Welfare and Development",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}