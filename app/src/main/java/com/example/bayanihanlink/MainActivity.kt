package com.example.bayanihanlink

import com.example.bayanihanlink.api.*
import com.example.bayanihanlink.individual.*
import com.example.bayanihanlink.donor.*
import com.example.bayanihanlink.individual.UpdateItem

import android.content.Context
import android.app.Activity
import android.net.Uri
import androidx.core.view.WindowCompat
import androidx.compose.runtime.SideEffect
import androidx.compose.material.icons.filled.Description
import androidx.compose.ui.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.bayanihanlink.ui.theme.BayanihanLinkTheme
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BayanihanLinkApp()
        }
    }
}

// Added "Register" to the list of screens the app can show.
private enum class AppScreen {
    Splash,
    Onboarding,
    Login,
    Biometric,
    Register,
    ForgotPassword,
    ForgotPasswordOtp,
    ForgotPasswordReset,
    Home,
    MyRequests,
    Alerts,
    Profile,
    RequestAssistance,
    RequestDetails,
    DonorHome,
    NeedDetails,
    OfferAssistance,
    MyOffers,
    DonorAlerts,
    EditProfile,
    ChangePassword,
    About,
    DonorProfile,
    DonorChangePassword,
    DonorAbout
}

@Composable
fun BayanihanLinkApp() {
    val context = LocalContext.current

    // Dark Mode is remembered in SharedPreferences, so the choice survives
    // closing and reopening the app.
    val preferences = remember {
        context.getSharedPreferences("bayanihan_prefs", Context.MODE_PRIVATE)
    }
    var darkMode by remember {
        mutableStateOf(preferences.getBoolean("dark_mode", false))
    }
    fun setDarkMode(enabled: Boolean) {
        darkMode = enabled
        preferences.edit().putBoolean("dark_mode", enabled).apply()
    }

    // Keep the phone's status bar / navigation bar icons readable: dark icons
    // on a light background, light icons on a dark background.
    SideEffect {
        val window = (context as? Activity)?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !darkMode
            isAppearanceLightNavigationBars = !darkMode
        }
    }

    // The account that's currently logged in (filled in after a successful login, restored from SharedPreferences)
    var loggedInUser by remember { mutableStateOf<UserResponse?>(AppSessionManager.loadUserSession(context)) }

    // Initial screen: if a valid session exists in SharedPreferences, skip splash/onboarding/login and go straight to Home!
    var currentScreen by remember {
        mutableStateOf(
            if (AppSessionManager.loadUserSession(context) != null) {
                if (AppSessionManager.loadUserSession(context)?.accountType == "DONOR_VOLUNTEER") AppScreen.DonorHome else AppScreen.Home
            } else {
                AppScreen.Splash
            }
        )
    }

    var selectedRequestId by remember { mutableStateOf("#BL-000234") }

    BayanihanLinkTheme(darkTheme = darkMode) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {

    // The Forgot Password flow is three screens in a row
    var forgotEmail by remember { mutableStateOf("") }
    var forgotCode by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    val userRequests = remember { mutableStateListOf<RequestResponse>() }
    val allNeeds = remember { mutableStateListOf<RequestResponse>() }
    val userOffers = remember { mutableStateListOf<OfferResponse>() }
    val notifications = remember { mutableStateListOf<NotificationResponse>() }
    val recentUpdates = remember { mutableStateListOf<UpdateItem>() }

    // Auto-load requests/needs/notifications on first composition if user session is restored
    LaunchedEffect(Unit) {
        val user = loggedInUser
        if (user != null) {
            coroutineScope.launch {
                try {
                    if (user.accountType == "DONOR_VOLUNTEER") {
                        val needs = RetrofitClient.api.getAllRequests()
                        allNeeds.clear()
                        allNeeds.addAll(needs)
                        val offers = RetrofitClient.api.getOffersByUser(user.id, user.id, if (user.authToken.isNullOrBlank()) null else "Bearer ${user.authToken}", user.authToken)
                        userOffers.clear()
                        userOffers.addAll(offers)
                    } else {
                        val reqs = RetrofitClient.api.getUserRequests(user.id, user.id, if (user.authToken.isNullOrBlank()) null else "Bearer ${user.authToken}", user.authToken)
                        userRequests.clear()
                        userRequests.addAll(reqs)
                        
                        // Populate recent updates from user requests
                        recentUpdates.clear()
                        reqs.take(5).forEach { r ->
                            val reqIdFormatted = AppSessionManager.getFormattedRequestId(context, r.id, r.dateSubmitted)
                            val statusText = when (r.status) {
                                "VERIFIED" -> "Your request $reqIdFormatted has been verified by the administrator."
                                "ASSISTED", "COMPLETED" -> "Your request $reqIdFormatted has been completed."
                                else -> "Your request $reqIdFormatted has been submitted."
                            }
                            val bg = when (r.status) {
                                "VERIFIED", "ASSISTED", "COMPLETED" -> Color(0xFF3BB273)
                                else -> Color(0xFF2B49CC)
                            }
                            recentUpdates.add(UpdateItem(
                                icon = Icons.Default.Description,
                                iconBackground = bg,
                                message = statusText,
                                timeAgo = r.dateSubmitted?.take(10) ?: "Recent"
                            ))
                        }
                    }
                    val notifs = RetrofitClient.api.getNotifications(user.id, user.id, if (user.authToken.isNullOrBlank()) null else "Bearer ${user.authToken}", user.authToken)
                    notifications.clear()
                    notifications.addAll(notifs)
                } catch (_: Exception) {}
            }
        }
    }

    // Used by the Alerts screens to show loading / error states.
    var notificationsLoading by remember { mutableStateOf(false) }
    var notificationsError by remember { mutableStateOf<String?>(null) }

    // (Re)loads the signed-in user's notifications from the backend.
    fun loadNotifications() {
        val user = loggedInUser ?: return
        notificationsLoading = true
        notificationsError = null
        coroutineScope.launch {
            try {
                val notifs = RetrofitClient.api.getNotifications(
                    user.id,
                    user.id,
                    if (user.authToken.isNullOrBlank()) null else "Bearer ${user.authToken}",
                    user.authToken
                )
                notifications.clear()
                notifications.addAll(notifs)
            } catch (error: Exception) {
                notificationsError = readableErrorMessage(error)
            } finally {
                notificationsLoading = false
            }
        }
    }

    // Refresh notifications every time the user opens an Alerts screen, so
    // they always see real, up-to-date events from the backend.
    LaunchedEffect(currentScreen) {
        if (currentScreen == AppScreen.Alerts || currentScreen == AppScreen.DonorAlerts) {
            loadNotifications()
        }
    }

    // Marks ONE notification as read (called when the user taps an alert).
    fun markNotificationAsRead(notificationId: String?) {
        val user = loggedInUser ?: return
        if (notificationId.isNullOrBlank()) return
        val index = notifications.indexOfFirst { (it._id ?: it.id) == notificationId }
        if (index >= 0 && notifications[index].isRead == true) return
        coroutineScope.launch {
            try {
                RetrofitClient.api.markNotificationRead(
                    notificationId,
                    user.id,
                    if (user.authToken.isNullOrBlank()) null else "Bearer ${user.authToken}",
                    user.authToken
                )
                if (index >= 0) {
                    notifications[index] = notifications[index].copy(isRead = true)
                }
            } catch (_: Exception) {
            }
        }
    }

    // Whenever some screen wants to go "back to Home", this decides WHICH
    // home screen that means — Affected Individuals and Donors/Volunteers
    // each have their own.
    fun homeScreenForCurrentUser(): AppScreen {
        return if (loggedInUser?.accountType == "DONOR_VOLUNTEER") {
            AppScreen.DonorHome
        } else {
            AppScreen.Home
        }
    }

    // Same idea, but for "go back to Profile" — Donors and Affected
    // Individuals each have their own separate Profile screen.
    fun profileScreenForCurrentUser(): AppScreen {
        return if (loggedInUser?.accountType == "DONOR_VOLUNTEER") {
            AppScreen.DonorProfile
        } else {
            AppScreen.Profile
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
                            AppSessionManager.saveUserSession(context, user)
                            currentScreen = if (user.accountType == "DONOR_VOLUNTEER") {
                                AppScreen.DonorHome
                            } else {
                                AppScreen.Home
                            }
                            // Load data
                            try {
                                if (user.accountType == "DONOR_VOLUNTEER") {
                                    val needs = RetrofitClient.api.getAllRequests()
                                    allNeeds.clear()
                                    allNeeds.addAll(needs)
                                    val offers = RetrofitClient.api.getOffersByUser(user.id, user.id, if (user.authToken.isNullOrBlank()) null else "Bearer ${user.authToken}", user.authToken)
                                    userOffers.clear()
                                    userOffers.addAll(offers)
                                } else {
                                    val reqs = RetrofitClient.api.getUserRequests(user.id, user.id, if (user.authToken.isNullOrBlank()) null else "Bearer ${user.authToken}", user.authToken)
                                    userRequests.clear()
                                    userRequests.addAll(reqs)
                                }
                                val notifs = RetrofitClient.api.getNotifications(user.id, user.id, if (user.authToken.isNullOrBlank()) null else "Bearer ${user.authToken}", user.authToken)
                                notifications.clear()
                                notifications.addAll(notifs)
                            } catch (_: Exception) {
                            }
                        } catch (error: Exception) {
                            Toast.makeText(context, readableErrorMessage(error), Toast.LENGTH_LONG).show()
                        }
                    }
                },
                onCreateAccount = {
                    // Tapping "Create an Account" now opens the Register screen.
                    currentScreen = AppScreen.Register
                },
                onForgotPassword = {
                    // Tapping "Forgot Password?" on the Login screen opens the
                    // Forgot Password flow (email -> code -> new password).
                    currentScreen = AppScreen.ForgotPassword
                },
                onAdminLogin = {

                },
                onFingerprintLogin = {
                    currentScreen = AppScreen.Biometric
                }
            )
            AppScreen.Biometric -> BiometricScreen(
                context = context,
                onAuthenticated = {
                    // After successful fingerprint auth, go to home (user must have
                    // an existing session; in a real app we'd restore it from secure storage).
                    // For now, we just return to login so the user enters credentials once.
                    currentScreen = AppScreen.Login
                    Toast.makeText(context, "Biometric authenticated.", Toast.LENGTH_SHORT).show()
                },
                onSkip = {
                    currentScreen = AppScreen.Login
                }
            )
            AppScreen.ForgotPassword -> ForgotPasswordScreen(
                onBack = {
                    // Back arrow -> return to Login
                    currentScreen = AppScreen.Login
                },
                onContinue = { email ->
                    // Step 1 done. Keep the email and show the code screen.
                    forgotEmail = email
                    currentScreen = AppScreen.ForgotPasswordOtp
                }
            )
            AppScreen.ForgotPasswordOtp -> ForgotPasswordOtpScreen(
                email = forgotEmail,
                onBack = {
                    // Back arrow -> go back to the email screen
                    currentScreen = AppScreen.ForgotPassword
                },
                onContinue = { code ->
                    // Step 2 done. Keep the code and show the new password screen.
                    forgotCode = code
                    currentScreen = AppScreen.ForgotPasswordReset
                }
            )
            AppScreen.ForgotPasswordReset -> ForgotPasswordResetScreen(
                email = forgotEmail,
                code = forgotCode,
                onBack = {
                    // Back arrow -> go back to the code screen
                    currentScreen = AppScreen.ForgotPasswordOtp
                },
                onFinished = {
                    // The password is changed, so send them to log in again.
                    currentScreen = AppScreen.Login
                }
            )
            AppScreen.Register -> RegisterScreen(
                onBack = {
                    // Back arrow -> return to Login
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
                                        // formData.accountType.name turns the enum into the exact
                                        // text the backend expects, e.g. "AFFECTED_INDIVIDUAL".
                                        accountType = formData.accountType.name
                                    )
                                )
                                Toast.makeText(context, "Account created! Please check your email to verify your account before logging in.", Toast.LENGTH_LONG).show()
                                currentScreen = AppScreen.Login
                            } catch (error: Exception) {
                                Toast.makeText(context, readableErrorMessage(error), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                onLogIn = {
                    // "Already have an account? Log in" -> return to Login
                    currentScreen = AppScreen.Login
                },
                onTermsClick = {

                },
                onPrivacyClick = {

                }
            )
            AppScreen.Home -> {
                val active = userRequests.firstOrNull()
                val mapStatus = { s: String? -> when (s) { "PENDING" -> 1; "VERIFIED" -> 2; "ASSISTED" -> 3; "COMPLETED" -> 4; else -> 1 } }
                HomeScreen(
                    userName = loggedInUser?.fullName ?: "Citizen",
                    activeRequestId = if (active != null) AppSessionManager.getFormattedRequestId(context, active.id, active.dateSubmitted) else null,
                    activeRequestTitle = active?.disasterType ?: "",
                    activeRequestItems = active?.itemNeeded ?: "",
                    activeRequestUrgency = active?.urgency ?: "",
                    activeRequestStatusLabel = active?.status ?: "",
                    activeRequestStep = mapStatus(active?.status),
                    totalRequests = userRequests.size,
                    verifiedRequests = userRequests.count { it.status == "VERIFIED" },
                    completedRequests = userRequests.count { it.status == "COMPLETED" },
                    recentUpdates = recentUpdates,
                    onRequestAssistance = {
                        currentScreen = AppScreen.RequestAssistance
                    },
                    onViewRequestDetails = {
                        if (active != null) selectedRequestId = active.id
                        currentScreen = AppScreen.RequestDetails
                    },
                    onUpdateClick = { _ ->
                        // A recent update has no request id of its own, so open
                        // the list where the user can pick the right request.
                        currentScreen = AppScreen.MyRequests
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
            }
            AppScreen.MyRequests -> MyRequestsScreen(
                requests = userRequests.map { r ->
                    com.example.bayanihanlink.individual.RequestItem(
                        id = AppSessionManager.getFormattedRequestId(context, r.id, r.dateSubmitted),
                        backendId = r.id,
                        title = r.disasterType ?: (r.categories?.firstOrNull() ?: ""),
                        items = r.itemNeeded ?: "",
                        quantity = "${r.quantity ?: ""} ${r.unit ?: ""}".trim(),
                        date = r.dateSubmitted?.take(10) ?: "",
                        urgency = r.urgency ?: "NORMAL",
                        status = when (r.status) {
                            "VERIFIED" -> com.example.bayanihanlink.individual.RequestStatus.VERIFIED
                            "ASSISTED" -> com.example.bayanihanlink.individual.RequestStatus.ASSISTED
                            "COMPLETED" -> com.example.bayanihanlink.individual.RequestStatus.COMPLETED
                            else -> com.example.bayanihanlink.individual.RequestStatus.PENDING
                        },
                        recencyOrder = 1
                    )
                },
                onViewDetails = { request ->
                    selectedRequestId = request.backendId
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
                notificationsList = notifications.map { n ->
                    val icon = when (n.type) {
                        "REQUEST_VERIFIED", "REQUEST_STATUS_UPDATE", "REQUEST_COMPLETED" -> Icons.Filled.CheckCircle
                        "OFFER_RECEIVED", "OFFER_ACCEPTED" -> Icons.Filled.Favorite
                        else -> Icons.Filled.Notifications
                    }
                    val bg = when (n.type) {
                        "REQUEST_VERIFIED" -> androidx.compose.ui.graphics.Color(0xFF3BB273)
                        "OFFER_ACCEPTED" -> androidx.compose.ui.graphics.Color(0xFFF5A623)
                        "REQUEST_COMPLETED" -> androidx.compose.ui.graphics.Color(0xFF3BB273)
                        else -> androidx.compose.ui.graphics.Color(0xFF9C4DCC)
                    }
                    com.example.bayanihanlink.individual.NotificationItem(
                        icon = icon,
                        iconBackground = bg,
                        title = n.title ?: "",
                        message = n.message ?: "",
                        timeAgo = n.createdAt?.take(10) ?: "",
                        isUnread = !(n.isRead ?: false),
                        requestId = n.requestId ?: n.targetId,
                        type = n.type,
                        notificationId = n._id ?: n.id
                    )
                },
                isLoading = notificationsLoading,
                errorMessage = notificationsError,
                onRetry = { loadNotifications() },
                onMarkAllRead = {
                    val user = loggedInUser
                    if (user != null) {
                        coroutineScope.launch {
                            try {
                                RetrofitClient.api.markAllNotificationsRead(
                                    user.id,
                                    user.id,
                                    if (user.authToken.isNullOrBlank()) null else "Bearer ${user.authToken}",
                                    user.authToken
                                )
                            } catch (_: Exception) {
                            }
                            loadNotifications()
                        }
                    }
                },
                onNotificationClick = { notification ->
                    val requestId = notification.requestId
                    if (!requestId.isNullOrBlank()) {
                        selectedRequestId = requestId
                        currentScreen = AppScreen.RequestDetails
                    }
                    markNotificationAsRead(notification.notificationId)
                },
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
            // Affected Individual's Profile screen.
            AppScreen.Profile -> ProfileScreen(
                user = loggedInUser.toUserProfile(),
                onEditProfile = {
                    currentScreen = AppScreen.EditProfile
                },
                onChangePassword = {
                    currentScreen = AppScreen.ChangePassword
                },
                onMyRequests = {
                    currentScreen = AppScreen.MyRequests
                },
                onAboutBayanihanLink = {
                    currentScreen = AppScreen.About
                },
                onLogOut = {
                    loggedInUser = null
                    AppSessionManager.saveUserSession(context, null)
                    currentScreen = AppScreen.Login
                },
                onNavigateHome = {
                    currentScreen = AppScreen.Home
                },
                onNavigateMyRequest = {
                    currentScreen = AppScreen.MyRequests
                },
                onNavigateAlerts = {
                    currentScreen = AppScreen.Alerts
                },
                onToggleDarkMode = {
                    setDarkMode(!darkMode)
                }
            )
            // Donor/Volunteer's Profile screen — a separate screen entirely,
            // not just a different mode of the one above.
            AppScreen.DonorProfile -> DonorProfileScreen(
                user = loggedInUser.toUserProfile(),
                onEditProfile = {
                    currentScreen = AppScreen.EditProfile
                },
                onChangePassword = {
                    currentScreen = AppScreen.DonorChangePassword
                },
                onAboutBayanihanLink = {
                    currentScreen = AppScreen.DonorAbout
                },
                onLogOut = {
                    loggedInUser = null
                    AppSessionManager.saveUserSession(context, null)
                    currentScreen = AppScreen.Login
                },
                onNavigateNeeds = {
                    currentScreen = AppScreen.DonorHome
                },
                onNavigateMyOffers = {
                    currentScreen = AppScreen.MyOffers
                },
                onNavigateAlerts = {
                    currentScreen = AppScreen.DonorAlerts
                },
                onToggleDarkMode = {
                    setDarkMode(!darkMode)
                }
            )
            AppScreen.EditProfile -> EditProfileScreen(
                userId = loggedInUser?.id ?: "",
                user = loggedInUser.toUserProfile(),
                authToken = loggedInUser?.authToken ?: "",
                onBack = {
                    currentScreen = profileScreenForCurrentUser()
                },
                onSaved = { updatedUser ->
                    loggedInUser = updatedUser
                    AppSessionManager.saveUserSession(context, updatedUser)
                    currentScreen = profileScreenForCurrentUser()
                }
            )
            AppScreen.ChangePassword -> ChangePasswordScreen(
                userId = loggedInUser?.id ?: "",
                onBack = {
                    currentScreen = AppScreen.Profile
                }
            )
            AppScreen.DonorChangePassword -> DonorChangePasswordScreen(
                userId = loggedInUser?.id ?: "",
                onBack = {
                    currentScreen = AppScreen.DonorProfile
                }
            )
            AppScreen.About -> AboutScreen(
                onBack = {
                    currentScreen = AppScreen.Profile
                }
            )
            AppScreen.DonorAbout -> DonorAboutScreen(
                onBack = {
                    currentScreen = AppScreen.DonorProfile
                }
            )
            AppScreen.RequestAssistance -> {
                var evidenceUri by remember { mutableStateOf<Uri?>(null) }
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    if (uri != null) {
                        // Check the file type and size before using it.
                        val problem = validateEvidenceImage(context, uri)
                        if (problem != null) {
                            Toast.makeText(context, problem, Toast.LENGTH_LONG).show()
                        } else {
                            evidenceUri = uri
                        }
                    }
                }
                RequestAssistanceScreen(
                    userAddress = loggedInUser?.address ?: "",
                    userContact = loggedInUser?.contactNumber ?: "",
                    evidenceUri = evidenceUri,
                    onUploadImage = { launcher.launch("image/*") },
                    onImageRemoved = { evidenceUri = null },
                    onExit = {
                        currentScreen = homeScreenForCurrentUser()
                    },
                    onViewMyRequest = {
                        currentScreen = AppScreen.MyRequests
                    },
                    onBackToHome = {
                        currentScreen = homeScreenForCurrentUser()
                    },
                    onSubmitRequest = { payload ->
                        val user = loggedInUser
                        if (user == null) {
                            Toast.makeText(context, "Please log in again.", Toast.LENGTH_LONG).show()
                            return@RequestAssistanceScreen
                        }
                        coroutineScope.launch {
                            try {
                                val disasterType = (payload["disaster"] as? String) ?: ""
                                val categories = payload["categories"] as? List<*>
                                val category = categories?.firstOrNull()?.toString() ?: ""
                                val categoriesStr = org.json.JSONArray(categories ?: emptyList<Any>()).toString()
                                val itemNeeded = payload["itemNeeded"] as? String ?: ""
                                val quantity = payload["quantity"] as? String ?: ""
                                val unit = payload["unit"] as? String ?: ""
                                val description = payload["description"] as? String ?: ""
                                val affectedPersons = (payload["affectedPersons"] as? Int) ?: 0
                                val location = user.address

                                val textMedia = "text/plain".toMediaTypeOrNull()
                                val userIdBody = user.id.toRequestBody(textMedia)
                                val disasterBody = disasterType.toRequestBody(textMedia)
                                val categoryBody = category.toRequestBody(textMedia)
                                val categoriesBody = categoriesStr.toRequestBody(textMedia)
                                val itemBody = itemNeeded.toRequestBody(textMedia)
                                val qtyBody = quantity.toRequestBody(textMedia)
                                val unitBody = unit.toRequestBody(textMedia)
                                val descBody = description.toRequestBody(textMedia)
                                val personsBody = affectedPersons.toString().toRequestBody(textMedia)
                                val locBody = location.toRequestBody(textMedia)

                                var part: MultipartBody.Part? = null
                                if (evidenceUri != null) {
                                    try {
                                        val inputStream = context.contentResolver.openInputStream(evidenceUri!!)
                                        val file = File(context.cacheDir, "evidence.jpg")
                                        inputStream?.use { input ->
                                            file.outputStream().use { output -> input.copyTo(output) }
                                        }
                                        val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                        part = MultipartBody.Part.createFormData("evidenceImage", file.name, reqFile)
                                    } catch (_: Exception) {
                                    }
                                }

                                val resp = RetrofitClient.api.createRequest(
                                    userIdBody,
                                    disasterBody,
                                    categoryBody,
                                    categoriesBody,
                                    itemBody,
                                    qtyBody,
                                    unitBody,
                                    descBody,
                                    personsBody,
                                    locBody,
                                    part,
                                    user.id,
                                    if (user.authToken.isNullOrBlank()) null else "Bearer ${user.authToken}",
                                    user.authToken
                                )
                                Toast.makeText(context, "Request submitted", Toast.LENGTH_SHORT).show()
                                try {
                                    val reqs = RetrofitClient.api.getUserRequests(user.id, user.id, if (user.authToken.isNullOrBlank()) null else "Bearer ${user.authToken}", user.authToken)
                                    userRequests.clear()
                                    userRequests.addAll(reqs)

                                    // Add to Recent Updates so it shows on Home screen
                                    val disasterLabel = disasterType
                                    val itemLabel = if (itemNeeded.isNotBlank()) itemNeeded else "Assistance request"
                                    val accentBlue = Color(0xFF2B49CC)
                                    val updateItem = UpdateItem(
                                        icon = Icons.Default.Description,
                                        iconBackground = accentBlue,
                                        message = "Request submitted: $itemLabel ($disasterLabel)",
                                        timeAgo = "Just now"
                                    )
                                    recentUpdates.add(0, updateItem)
                                } catch (_: Exception) {}
                                currentScreen = AppScreen.MyRequests
                            } catch (error: Exception) {
                                Toast.makeText(context, readableErrorMessage(error), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }
            AppScreen.RequestDetails -> {
                // Use the request we already loaded (if we have it) so the
                // photo and details belong to exactly this request.
                val request = userRequests.find { it.id == selectedRequestId }
                    ?: allNeeds.find { it.id == selectedRequestId }
                RequestDetailsScreen(
                    requestId = selectedRequestId,
                    detail = request?.toRequestDetail(),
                    onBack = {
                        currentScreen = if (loggedInUser?.accountType == "DONOR_VOLUNTEER") {
                            AppScreen.DonorHome
                        } else {
                            AppScreen.MyRequests
                        }
                    }
                )
            }
            AppScreen.DonorHome -> DonorHomeScreen(
                userName = loggedInUser?.fullName ?: "Guest",
                needs = allNeeds.map { r ->
                    com.example.bayanihanlink.donor.CommunityNeed(
                        id = r.id,
                        urgency = r.urgency ?: "NORMAL",
                        title = r.disasterType ?: "",
                        itemName = r.itemNeeded ?: "",
                        neededQuantity = "${r.quantity ?: ""} ${r.unit ?: ""}".trim(),
                        description = r.description ?: "",
                        location = r.location ?: "",
                        date = r.dateSubmitted?.take(10) ?: "",
                        evidenceImageUrl = if (r.evidenceImage.isNullOrBlank()) "" else RetrofitClient.getImageUrl(r.evidenceImage!!)
                    )
                },
                onViewRequest = { needId ->
                    selectedRequestId = needId
                    currentScreen = AppScreen.NeedDetails
                },
                onNavigateMyOffers = {
                    currentScreen = AppScreen.MyOffers
                },
                onNavigateAlerts = {
                    currentScreen = AppScreen.DonorAlerts
                },
                onNavigateProfile = {
                    currentScreen = AppScreen.DonorProfile
                }
            )
            AppScreen.MyOffers -> MyOffersScreen(
                offers = userOffers.map { o ->
                    com.example.bayanihanlink.donor.DonationOffer(
                        id = o._id ?: o.id ?: "",
                        itemName = "",
                        quantity = "${o.quantityOffered ?: ""} ${o.unit ?: ""}".trim(),
                        relatedRequestId = o.requestId ?: "",
                        date = o.createdAt?.take(10) ?: "",
                        status = when (o.status) {
                            "ACCEPTED", "DISTRIBUTED" -> com.example.bayanihanlink.donor.OfferStatus.DISTRIBUTED
                            "COMPLETED" -> com.example.bayanihanlink.donor.OfferStatus.COMPLETED
                            else -> com.example.bayanihanlink.donor.OfferStatus.COORDINATING
                        },
                        coordinatorNote = o.message
                    )
                },
                onNavigateNeeds = {
                    currentScreen = AppScreen.DonorHome
                },
                onNavigateAlerts = {
                    currentScreen = AppScreen.DonorAlerts
                },
                onNavigateProfile = {
                    currentScreen = AppScreen.DonorProfile
                }
            )
            AppScreen.DonorAlerts -> DonorAlertsScreen(
                notificationsList = notifications.map { n ->
                    val icon = when (n.type) {
                        "OFFER_ACCEPTED" -> Icons.Filled.CheckCircle
                        "OFFER_RECEIVED" -> Icons.Filled.Favorite
                        else -> Icons.Filled.Notifications
                    }
                    val bg = when (n.type) {
                        "OFFER_ACCEPTED" -> androidx.compose.ui.graphics.Color(0xFF3BB273)
                        "OFFER_RECEIVED" -> androidx.compose.ui.graphics.Color(0xFFF5A623)
                        else -> androidx.compose.ui.graphics.Color(0xFF9C4DCC)
                    }
                    com.example.bayanihanlink.donor.DonorNotification(
                        icon = icon,
                        iconBackground = bg,
                        title = n.title ?: "",
                        message = n.message ?: "",
                        timeAgo = n.createdAt?.take(10) ?: "",
                        isUnread = !(n.isRead ?: false),
                        requestId = n.requestId ?: n.targetId,
                        type = n.type,
                        notificationId = n._id ?: n.id
                    )
                },
                isLoading = notificationsLoading,
                errorMessage = notificationsError,
                onRetry = { loadNotifications() },
                onMarkAllRead = {
                    val user = loggedInUser
                    if (user != null) {
                        coroutineScope.launch {
                            try {
                                RetrofitClient.api.markAllNotificationsRead(
                                    user.id,
                                    user.id,
                                    if (user.authToken.isNullOrBlank()) null else "Bearer ${user.authToken}",
                                    user.authToken
                                )
                            } catch (_: Exception) {
                            }
                            loadNotifications()
                        }
                    }
                },
                onNotificationClick = { notification ->
                    // Donors go to the Donor "request details" (NeedDetails) page.
                    val requestId = notification.requestId
                    if (!requestId.isNullOrBlank()) {
                        selectedRequestId = requestId
                        currentScreen = AppScreen.NeedDetails
                    }
                    markNotificationAsRead(notification.notificationId)
                },
                onNavigateNeeds = {
                    currentScreen = AppScreen.DonorHome
                },
                onNavigateMyOffers = {
                    currentScreen = AppScreen.MyOffers
                },
                onNavigateProfile = {
                    currentScreen = AppScreen.DonorProfile
                }
            )
            AppScreen.NeedDetails -> {
                // Use the need we already loaded so this page matches the card
                // (and the photo) the donor tapped on.
                val need = allNeeds.find { it.id == selectedRequestId }
                com.example.bayanihanlink.donor.NeedDetailsScreen(
                    needId = selectedRequestId,
                    detail = need?.toNeedDetail(),
                    onBack = {
                        currentScreen = AppScreen.DonorHome
                    },
                    onOfferAssistance = {
                        currentScreen = AppScreen.OfferAssistance
                    }
                )
            }
            AppScreen.OfferAssistance -> OfferAssistanceScreen(
                needId = selectedRequestId,
                needContact = loggedInUser?.contactNumber ?: "",
                onBack = {
                    currentScreen = AppScreen.NeedDetails
                },
                onViewMyOffers = {
                    currentScreen = AppScreen.MyOffers
                },
                onBackToNeedsBoard = {
                    currentScreen = AppScreen.DonorHome
                },
                onSubmitOffer = { payload ->
                    val user = loggedInUser
                    if (user == null) {
                        Toast.makeText(context, "Please log in again.", Toast.LENGTH_LONG).show()
                        return@OfferAssistanceScreen
                    }
                    coroutineScope.launch {
                        try {
                            val qty = payload["quantityAvailable"] as? String ?: ""
                            val unit = payload["unit"] as? String ?: ""
                            val notes = payload["additionalNotes"] as? String ?: payload["description"] as? String ?: ""
                            val offerReq = OfferRequest(
                                donorId = user.id,
                                requestId = selectedRequestId,
                                quantityOffered = qty,
                                unit = unit,
                                message = notes
                            )
                            RetrofitClient.api.createOffer(
                                offerReq,
                                user.id,
                                if (user.authToken.isNullOrBlank()) null else "Bearer ${user.authToken}",
                                user.authToken
                            )
                            Toast.makeText(context, "Offer submitted", Toast.LENGTH_SHORT).show()
                            try {
                                val offers = RetrofitClient.api.getOffersByUser(user.id, user.id, if (user.authToken.isNullOrBlank()) null else "Bearer ${user.authToken}", user.authToken)
                                userOffers.clear()
                                userOffers.addAll(offers)
                                loadNotifications()
                            } catch (_: Exception) {}
                            currentScreen = AppScreen.DonorAlerts
                        } catch (error: Exception) {
                            Toast.makeText(context, readableErrorMessage(error), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            )
        }
    }
}
}
}
// Checks a photo the user picked. Returns an error message, or null if it's OK.
private fun validateEvidenceImage(context: Context, uri: Uri): String? {
    val mimeType = context.contentResolver.getType(uri)
    if (mimeType != null && !mimeType.startsWith("image/")) {
        return "Please choose an image file (JPG or PNG)."
    }
    val maxSize = 5 * 1024 * 1024 // 5 MB
    val size = try {
        context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: -1L
    } catch (_: Exception) {
        -1L
    }
    if (size > maxSize) {
        return "That image is too large. Please choose a photo under 5 MB."
    }
    return null
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
        // The backend stores "AFFECTED_INDIVIDUAL" — turn it into readable text.
        accountType = when (accountType) {
            "AFFECTED_INDIVIDUAL" -> "Citizen"
            "DONOR_VOLUNTEER" -> "Donor / Volunteer"
            else -> accountType
        },
        // These 3 are 0 for now, since we haven't connected the requests list
        // to the backend yet. That's the NEXT step (My Requests + Home stats).
        totalRequests = 0,
        verifiedRequests = 0,
        completedRequests = 0
    )
}