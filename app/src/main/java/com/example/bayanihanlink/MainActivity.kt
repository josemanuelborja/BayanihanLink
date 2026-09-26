package com.example.bayanihanlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.bayanihanlink.ui.theme.BayanihanLinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BayanihanLinkTheme {
                Surface(modifier = Modifier.fillMaxSize() .statusBarsPadding()) {

                    BayanihanLinkApp()
                }
            }
        }
    }
}
private enum class AppScreen {
    Splash,
    Onboarding,
    Login,
    Register,
    Home,
    MyRequests,
    Alerts
}

@Composable
fun BayanihanLinkApp() {
    var currentScreen by remember { mutableStateOf(AppScreen.Splash) }

    Crossfade(
        targetState = currentScreen,
        animationSpec = tween(durationMillis = 500), // fade takes 0.5 seconds
        label = "screenCrossfade"
    ) { screen ->
        when (screen) {
            AppScreen.Splash -> SplashScreen(
                onFinished = {
                    currentScreen = AppScreen.Onboarding
                }
            )
            AppScreen.Onboarding -> OnboardingScreen(
                onDone = {
                    currentScreen = AppScreen.Login
                },
                onLogIn = {
                    currentScreen = AppScreen.Login
                }
            )
            AppScreen.Login -> LoginScreen(
                onLogIn = { email, password ->
                    currentScreen = AppScreen.Home
                },
                onCreateAccount = {
                    currentScreen = AppScreen.Register
                },
                onForgotPassword = {

                },
                onAdminLogin = {

                }
            )
            AppScreen.Register -> RegisterScreen(
                onBack = {

                    currentScreen = AppScreen.Login
                },
                onRegister = { formData ->

                    currentScreen = AppScreen.Login
                },
                onLogIn = {

                    currentScreen = AppScreen.Login
                },
                onTermsClick = {

                },
                onPrivacyClick = {

                }
            )
            AppScreen.Home -> HomeScreen(
                onRequestAssistance = {

                },
                onNavigateMyRequest = {
                    currentScreen = AppScreen.MyRequests
                },
                onNavigateAlerts = {
                    currentScreen = AppScreen.Alerts
                },
                onNavigateProfile = {

                }
            )
            AppScreen.MyRequests -> MyRequestsScreen(
                onViewDetails = { request ->

                },
                onNavigateHome = {
                    currentScreen = AppScreen.Home
                },
                onNavigateAlerts = {
                    currentScreen = AppScreen.Alerts
                },
                onNavigateProfile = {

                }
            )
            AppScreen.Alerts -> AlertsScreen(
                onNavigateHome = {
                    currentScreen = AppScreen.Home
                },
                onNavigateMyRequest = {
                    currentScreen = AppScreen.MyRequests
                },
                onNavigateProfile = {

                }
            )
        }
    }
}