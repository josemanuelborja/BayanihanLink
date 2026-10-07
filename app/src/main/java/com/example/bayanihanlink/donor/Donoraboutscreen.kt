package com.example.bayanihanlink.donor

import com.example.bayanihanlink.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AccentBlue = Color(0xFF2B49CC)

@Composable
fun DonorAboutScreen(onBack: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AccentBlue)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.bayanihanlink_about),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 32.dp)
            ) {
            }

            IconButton(onClick = onBack, modifier = Modifier.padding(top = 4.dp, start = 4.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Go back", tint = Color.White)
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            AboutSection(
                title = "About Us",
                body = "BayanihanLink is a mobile application created to support communities during disasters by making assistance more organized. It provides a centralized platform where affected individuals can share their needs, while donors and volunteers can offer available assistance. It brings information together for easier coordination and monitoring."
            )
            AboutSection(
                title = "Our Purpose",
                body = "Our purpose is to connect real needs with available help through one organized platform. It helps affected individuals communicate their needs and allows donors and volunteers to identify where assistance is needed. Through BayanihanLink, communities can coordinate support more easily."
            )
            AboutSection(
                title = "Our Approach",
                body = "Our approach focuses on a needs-first process that begins with identifying actual disaster-related needs. Requests can be submitted, reviewed, and matched with available assistance. The system then helps monitor the progress of each request."
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AboutSection(title: String, body: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(3.dp)
                .background(Color.White, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(body, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp, lineHeight = 20.sp)
    }
}