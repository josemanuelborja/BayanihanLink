package com.example.bayanihanlink

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.bayanihanlink.ui.theme.BayanihanLinkTheme
import kotlinx.coroutines.launch

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
    Alerts,
    Profile,
    RequestAssistance,
    RequestDetails,
    DonorHome,
    NeedDetails,
    OfferAssistance
}

@Composable
fun BayanihanLinkApp() {
    var currentScreen by remember { mutableStateOf(AppScreen.Splash) }

    var selectedRequestId by remember { mutableStateOf("#BL-000234") }

    var loggedInUser by remember { mutableStateOf<UserResponse?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    fun homeScreenForCurrentUser(): AppScreen {
        return if (loggedInUser?.accountType == "DONOR_VOLUNTEER") {
            AppScreen.DonorHome
        } else {
            AppScreen.Home
        }
    }

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
                    coroutineScope.launch {
                        try {
                            val user = RetrofitClient.api.login(LoginRequest(email, password))
                            loggedInUser = user
                            currentScreen = if (user.accountType == "DONOR_VOLUNTEER") {
                                AppScreen.DonorHome
                            } else {
                                AppScreen.Home
                            }
                        } catch (error: Exception) {
                            Toast.makeText(context, readableErrorMessage(error), Toast.LENGTH_LONG).show()
                        }
                    }
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
                    if (formData.accountType == null) {
                        Toast.makeText(context, "Please select an account type.", Toast.LENGTH_LONG).show()
                    } else {
                        coroutineScope.launch {
                            try {
                                RetrofitClient.api.register(
                                    RegisterRequest(
                                        fullName = formData.fullName,
                                        email = formData.email,
                                        password = formData.password,
                                        contactNumber = formData.contactNumber,
                                        address = formData.address,
                                        accountType = formData.accountType.name
                                    )
                                )
                                Toast.makeText(context, "Account created! Please log in.", Toast.LENGTH_LONG).show()
                                currentScreen = AppScreen.Login
                            } catch (error: Exception) {
                                Toast.makeText(context, readableErrorMessage(error), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
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
                userName = loggedInUser?.fullName ?: "Maria Santos",
                onRequestAssistance = {
                    currentScreen = AppScreen.RequestAssistance
                },
                onViewRequestDetails = {
                    selectedRequestId = "#BL-000234"
                    currentScreen = AppScreen.RequestDetails
                },
                onNavigateMyRequest = {
                    currentScreen = AppScreen.MyRequests
                },
                onNavigateAlerts = {
                    currentScreen = AppScreen.Alerts
                },
                onNavigateProfile = {
                    currentScreen = AppScreen.Profile
                }
            )
            AppScreen.MyRequests -> MyRequestsScreen(
                onViewDetails = { request ->
                    selectedRequestId = request.id
                    currentScreen = AppScreen.RequestDetails
                },
                onNavigateHome = {
                    currentScreen = homeScreenForCurrentUser()
                },
                onNavigateAlerts = {
                    currentScreen = AppScreen.Alerts
                },
                onNavigateProfile = {
                    currentScreen = AppScreen.Profile
                }
            )
            AppScreen.Alerts -> AlertsScreen(
                onNavigateHome = {
                    currentScreen = homeScreenForCurrentUser()
                },
                onNavigateMyRequest = {
                    currentScreen = AppScreen.MyRequests
                },
                onNavigateProfile = {
                    currentScreen = AppScreen.Profile
                }
            )
            AppScreen.Profile -> ProfileScreen(
                user = loggedInUser.toUserProfile(),
                onEditProfile = {

                },
                onChangePassword = {

                },
                onNotificationPreferences = {

                },
                onMyRequests = {
                    currentScreen = AppScreen.MyRequests
                },
                onAboutBayanihanLink = {

                },
                onContactSupport = {

                },
                onLogOut = {
                    // Logging out sends the user back to the Login screen.
                    currentScreen = AppScreen.Login
                },
                onNavigateHome = {
                    currentScreen = homeScreenForCurrentUser()
                },
                onNavigateMyRequest = {
                    currentScreen = AppScreen.MyRequests
                },
                onNavigateAlerts = {
                    currentScreen = AppScreen.Alerts
                }
            )
            AppScreen.RequestAssistance -> RequestAssistanceScreen(
                onExit = {
                    currentScreen = homeScreenForCurrentUser()
                },
                onViewMyRequest = {
                    currentScreen = AppScreen.MyRequests
                },
                onBackToHome = {
                    currentScreen = homeScreenForCurrentUser()
                }
            )
            AppScreen.RequestDetails -> RequestDetailsScreen(
                requestId = selectedRequestId,
                onBack = {
                    currentScreen = if (loggedInUser?.accountType == "DONOR_VOLUNTEER") {
                        AppScreen.DonorHome
                    } else {
                        AppScreen.MyRequests
                    }
                }
            )
            AppScreen.DonorHome -> DonorHomeScreen(
                userName = loggedInUser?.fullName ?: "Guest",
                onViewRequest = { needId ->
                    selectedRequestId = needId
                    currentScreen = AppScreen.NeedDetails
                },
                onNavigateMyOffers = {

                },
                onNavigateAlerts = {
                    currentScreen = AppScreen.Alerts
                },
                onNavigateProfile = {
                    currentScreen = AppScreen.Profile
                }
            )
            AppScreen.NeedDetails -> NeedDetailsScreen(
                needId = selectedRequestId,
                onBack = {
                    currentScreen = AppScreen.DonorHome
                },
                onOfferAssistance = {
                    currentScreen = AppScreen.OfferAssistance
                }
            )
            AppScreen.OfferAssistance -> OfferAssistanceScreen(
                needId = selectedRequestId,
                onBack = {
                    currentScreen = AppScreen.NeedDetails
                },
                onViewMyOffers = {

                },
                onBackToNeedsBoard = {
                    currentScreen = AppScreen.DonorHome
                }
            )
        }
    }
}

private fun UserResponse?.toUserProfile(): UserProfile {
    if (this == null) {
        return UserProfile(
            fullName = "",
            email = "",
            contactNumber = "",
            address = "",
            accountType = "",
            totalRequests = 0,
            verifiedRequests = 0,
            completedRequests = 0
        )
    }

    return UserProfile(
        fullName = fullName,
        email = email,
        contactNumber = contactNumber,
        address = address,
        accountType = when (accountType) {
            "AFFECTED_INDIVIDUAL" -> "Affected Individual"
            "DONOR_VOLUNTEER" -> "Donor / Volunteer"
            else -> accountType
        },
        totalRequests = 0,
        verifiedRequests = 0,
        completedRequests = 0
    )
}