package com.example.bayanihanlink

import com.example.bayanihanlink.api.*
import com.example.bayanihanlink.individual.*
import com.example.bayanihanlink.donor.*

import android.net.Uri
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
import okhttp3.MediaType.Companion.toMediaType
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
            BayanihanLinkTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    BayanihanLinkApp()
                }
            }
        }
    }
}

// Added "Register" to the list of screens the app can show.
private enum class AppScreen {
    Splash,
    Onboarding,
    Login,
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
    var currentScreen by remember { mutableStateOf(AppScreen.Splash) }

    var selectedRequestId by remember { mutableStateOf("#BL-000234") }

    // The account that's currently logged in (filled in after a successful login)
    var loggedInUser by remember { mutableStateOf<UserResponse?>(null) }

    // The Forgot Password flow is three screens in a row
    var forgotEmail by remember { mutableStateOf("") }
    var forgotCode by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val userRequests = remember { mutableStateListOf<RequestResponse>() }
    val allNeeds = remember { mutableStateListOf<RequestResponse>() }
    val userOffers = remember { mutableStateListOf<OfferResponse>() }
    val notifications = remember { mutableStateListOf<NotificationResponse>() }

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
                                Toast.makeText(context, "Account created! Please log in.", Toast.LENGTH_LONG).show()
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
                    activeRequestId = active?.id,
                    activeRequestTitle = active?.disasterType ?: "",
                    activeRequestItems = active?.itemNeeded ?: "",
                    activeRequestUrgency = active?.urgency ?: "",
                    activeRequestStatusLabel = active?.status ?: "",
                    activeRequestStep = mapStatus(active?.status),
                    totalRequests = userRequests.size,
                    verifiedRequests = userRequests.count { it.status == "VERIFIED" },
                    completedRequests = userRequests.count { it.status == "COMPLETED" },
                    recentUpdates = emptyList(),
                    onRequestAssistance = {
                        currentScreen = AppScreen.RequestAssistance
                    },
                    onViewRequestDetails = {
                        if (active != null) selectedRequestId = active.id
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
            }
            AppScreen.MyRequests -> MyRequestsScreen(
                requests = userRequests.map { r ->
                    com.example.bayanihanlink.individual.RequestItem(
                        id = r.id,
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
                notificationsList = notifications.map { n ->
                    val icon = when (n.type) {
                        "REQUEST_VERIFIED", "REQUEST_STATUS_UPDATE", "REQUEST_COMPLETED" -> androidx.compose.material.icons.Icons.Filled.CheckCircle
                        "OFFER_RECEIVED", "OFFER_ACCEPTED" -> androidx.compose.material.icons.Icons.Filled.Favorite
                        else -> androidx.compose.material.icons.Icons.Filled.Notifications
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
                        type = n.type
                    )
                },
                onNotificationClick = { notification ->
                    if (notification.requestId != null) {
                        selectedRequestId = notification.requestId
                        currentScreen = AppScreen.RequestDetails
                    }
                    val n = notifications.find { (it._id == notification.requestId || it.requestId == notification.requestId) && it.isRead != true }
                    if (n != null && loggedInUser != null) {
                        coroutineScope.launch {
                            try {
                                val id = n._id ?: n.id ?: ""
                                if (id.isNotEmpty()) {
                                    RetrofitClient.api.markNotificationRead(id, loggedInUser!!.id, if (loggedInUser!!.authToken.isNullOrBlank()) null else "Bearer ${loggedInUser!!.authToken}", loggedInUser!!.authToken)
                                }
                            } catch (_: Exception) {}
                        }
                    }
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
                }
            )
            AppScreen.EditProfile -> EditProfileScreen(
                userId = loggedInUser?.id ?: "",
                user = loggedInUser.toUserProfile(),
                onBack = {
                    currentScreen = profileScreenForCurrentUser()
                },
                onSaved = { updatedUser ->
                    loggedInUser = updatedUser
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
                    evidenceUri = uri
                }
                RequestAssistanceScreen(
                    userAddress = loggedInUser?.address ?: "",
                    userContact = loggedInUser?.contactNumber ?: "",
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
                                } catch (_: Exception) {}
                                currentScreen = AppScreen.MyRequests
                            } catch (error: Exception) {
                                Toast.makeText(context, readableErrorMessage(error), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }
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
                needs = allNeeds.map { r ->
                    com.example.bayanihanlink.donor.CommunityNeed(
                        id = r.id,
                        urgency = r.urgency ?: "NORMAL",
                        title = r.disasterType ?: "",
                        itemName = r.itemNeeded ?: "",
                        neededQuantity = "${r.quantity ?: ""} ${r.unit ?: ""}".trim(),
                        description = r.description ?: "",
                        location = r.location ?: "",
                        date = r.dateSubmitted?.take(10) ?: ""
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
                        "OFFER_ACCEPTED" -> androidx.compose.material.icons.Icons.Filled.CheckCircle
                        "OFFER_RECEIVED" -> androidx.compose.material.icons.Icons.Filled.Favorite
                        else -> androidx.compose.material.icons.Icons.Filled.Notifications
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
                        type = n.type
                    )
                },
                onNotificationClick = { notification ->
                    if (notification.requestId != null) {
                        selectedRequestId = notification.requestId
                        currentScreen = AppScreen.NeedDetails
                    }
                    val n = notifications.find { (it._id == notification.requestId || it.requestId == notification.requestId) && it.isRead != true }
                    if (n != null && loggedInUser != null) {
                        coroutineScope.launch {
                            try {
                                val id = n._id ?: n.id ?: ""
                                if (id.isNotEmpty()) {
                                    RetrofitClient.api.markNotificationRead(id, loggedInUser!!.id, if (loggedInUser!!.authToken.isNullOrBlank()) null else "Bearer ${loggedInUser!!.authToken}", loggedInUser!!.authToken)
                                }
                            } catch (_: Exception) {}
                        }
                    }
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
            AppScreen.NeedDetails -> com.example.bayanihanlink.donor.NeedDetailsScreen(
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
                            } catch (_: Exception) {}
                            currentScreen = AppScreen.MyOffers
                        } catch (error: Exception) {
                            Toast.makeText(context, readableErrorMessage(error), Toast.LENGTH_LONG).show()
                        }
                    }
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
        // The backend stores "AFFECTED_INDIVIDUAL" — turn it into readable text.
        accountType = when (accountType) {
            "AFFECTED_INDIVIDUAL" -> "Affected Individual"
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