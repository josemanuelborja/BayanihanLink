package com.example.bayanihanlink.individual

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.example.bayanihanlink.ui.theme.AppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// These read the current theme colors, so Dark Mode works on this screen too.
private val AccentBlue: Color
    @Composable @ReadOnlyComposable get() = AppColors.accentBlue
private val LabelGray: Color
    @Composable @ReadOnlyComposable get() = AppColors.labelGray
private val BorderGray: Color
    @Composable @ReadOnlyComposable get() = AppColors.borderGray
private val DoneGreen = Color(0xFF3BB273)
private val FieldBackground: Color
    @Composable @ReadOnlyComposable get() = AppColors.fieldBackground
private val TextPrimary: Color
    @Composable @ReadOnlyComposable get() = AppColors.textPrimary

// The 5 disaster choices for Step 1.
enum class DisasterType(val label: String, val icon: ImageVector) {
    FLOOD("Flood", Icons.Filled.Waves),
    TYPHOON("Typhoon", Icons.Filled.Cloud),
    FIRE("Fire", Icons.Filled.Whatshot),
    EARTHQUAKE("Earthquake", Icons.Filled.Public),
    OTHER_DISASTER("Other", Icons.Filled.WarningAmber)
}

// The 7 assistance categories for Step 2.
enum class AssistanceCategory(val label: String, val icon: ImageVector) {
    FOOD("Food", Icons.Filled.ShoppingBag),
    DRINKING_WATER("Drinking Water", Icons.Filled.LocalDrink),
    MEDICINE("Medicine", Icons.Filled.LocalHospital),
    CLOTHING("Clothing", Icons.Filled.Checkroom),
    HYGIENE_SUPPLIES("Hygiene Supplies", Icons.Filled.CleaningServices),
    BLANKETS("Blankets", Icons.Filled.Bed),
    OTHER_NEED("Other", Icons.Filled.Category)
}

// The 4 steps of the wizard, plus a 5th "SUCCESS" screen shown after submitting.
private enum class WizardStep(val stepNumber: Int, val label: String) {
    DISASTER(1, "Disaster"),
    NEEDS(2, "Needs"),
    DETAILS(3, "Details"),
    REVIEW(4, "Review"),
    SUCCESS(5, "") // not shown in the step tracker
}

private val unitOptions = listOf("packs", "kg", "liters", "pieces", "sets", "boxes", "bottles")

// The form allows 1 or more affected persons.
private const val MIN_AFFECTED_PERSONS = 1

@Composable
fun RequestAssistanceScreen(
    userAddress: String = "",
    userContact: String = "",
    evidenceUri: Uri? = null,
    onUploadImage: () -> Unit = {},
    onImageRemoved: () -> Unit = {},
    onExit: () -> Unit = {},
    onViewMyRequest: () -> Unit = {},
    onBackToHome: () -> Unit = {},
    onSubmitRequest: (Map<String, Any>) -> Unit = {}
) {
    // Which step of the wizard we're currently showing.
    var currentStep by rememberSaveable { mutableStateOf(WizardStep.DISASTER) }

    // Everything the user picks/types, kept in one place.
    var selectedDisaster by rememberSaveable { mutableStateOf<DisasterType?>(null) }
    var selectedCategories by rememberSaveable { mutableStateOf(setOf<AssistanceCategory>()) }
    var itemNeeded by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("") }
    var unit by rememberSaveable { mutableStateOf(unitOptions.first()) }
    var description by rememberSaveable { mutableStateOf("") }
    var affectedPersons by rememberSaveable { mutableStateOf("") }

    // True when a photo has been picked (the parent screen owns the Uri).
    val hasEvidence = evidenceUri != null

    // The Next button on each step is only enabled once the required info is filled in.
    val isDisasterStepValid = selectedDisaster != null
    val isNeedsStepValid = selectedCategories.isNotEmpty()
    val affectedCount = affectedPersons.trim().toIntOrNull()
    val isDetailsStepValid = itemNeeded.isNotBlank() &&
        quantity.isNotBlank() &&
        affectedCount != null &&
        affectedCount >= 1 &&
        hasEvidence

    if (currentStep == WizardStep.SUCCESS) {
        RequestSubmittedScreen(
            onViewMyRequest = onViewMyRequest,
            onBackToHome = onBackToHome
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.background)
            .imePadding()
            .navigationBarsPadding()
    ) {
        // Top bar: back arrow + title.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
        ) {
            IconButton(onClick = onExit) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit", tint = TextPrimary)
            }
            Text("Request Assistance", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }

        WizardStepTracker(currentStep = currentStep)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            when (currentStep) {
                WizardStep.DISASTER -> DisasterStep(
                    selected = selectedDisaster,
                    onSelect = { selectedDisaster = it }
                )
                WizardStep.NEEDS -> NeedsStep(
                    selected = selectedCategories,
                    onToggle = { category ->
                        selectedCategories = if (selectedCategories.contains(category)) {
                            selectedCategories - category
                        } else {
                            selectedCategories + category
                        }
                    }
                )
                WizardStep.DETAILS -> DetailsStep(
                    itemNeeded = itemNeeded,
                    onItemNeededChange = { itemNeeded = it },
                    quantity = quantity,
                    onQuantityChange = { quantity = it },
                    unit = unit,
                    onUnitChange = { unit = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    affectedPersons = affectedPersons,
                    onAffectedPersonsChange = { newValue ->
                        // Only digits are allowed, and the field accepts at most
                        // 3 characters so the user can see and correct "14".
                        affectedPersons = newValue.filter { it.isDigit() }.take(3)
                    },
                    evidenceUri = evidenceUri,
                    onUploadImage = onUploadImage,
                    onImageRemoved = onImageRemoved
                )
                WizardStep.REVIEW -> ReviewStep(
                    disaster = selectedDisaster,
                    categories = selectedCategories,
                    itemNeeded = itemNeeded,
                    quantity = quantity,
                    unit = unit,
                    affectedPersons = affectedPersons,
                    address = userAddress,
                    contact = userContact
                )
                WizardStep.SUCCESS -> Unit
            }
        }

        // Bottom Back/Next (or Submit) buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            if (currentStep != WizardStep.DISASTER) {
                OutlinedButton(
                    onClick = {
                        currentStep = WizardStep.entries[currentStep.ordinal - 1]
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Back", color = AccentBlue, fontWeight = FontWeight.SemiBold)
                }
            }

            val isCurrentStepValid = when (currentStep) {
                WizardStep.DISASTER -> isDisasterStepValid
                WizardStep.NEEDS -> isNeedsStepValid
                WizardStep.DETAILS -> isDetailsStepValid
                WizardStep.REVIEW -> isDetailsStepValid
                WizardStep.SUCCESS -> true
            }

            Button(
                onClick = {
                    if (currentStep == WizardStep.REVIEW) {
                        val payload = mapOf(
                            "disaster" to (selectedDisaster?.label ?: ""),
                            "categories" to selectedCategories.map { it.label },
                            "itemNeeded" to itemNeeded,
                            "quantity" to quantity,
                            "unit" to unit,
                            "affectedPersons" to (affectedCount ?: 0),
                            "description" to description
                        )
                        onSubmitRequest(payload)
                        currentStep = WizardStep.SUCCESS
                    } else {
                        currentStep = WizardStep.entries[currentStep.ordinal + 1]
                    }
                },
                enabled = isCurrentStepValid,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text(
                    text = if (currentStep == WizardStep.REVIEW) "Submit Request" else "Next",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ---------- Step 1: "What type of disaster occurred?" ----------
@Composable
private fun DisasterStep(selected: DisasterType?, onSelect: (DisasterType) -> Unit) {
    Text("What type of disaster occurred?", fontSize = 17.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(4.dp))
    Text("Select the disaster that affected you.", fontSize = 13.sp, color = LabelGray)
    Spacer(modifier = Modifier.height(16.dp))

    OptionGrid(
        options = DisasterType.entries,
        selected = selected,
        onSelect = onSelect,
        label = { it.label },
        icon = { it.icon }
    )
}

// ---------- Step 2: "What type of assistance do you need?" ----------
@Composable
private fun NeedsStep(selected: Set<AssistanceCategory>, onToggle: (AssistanceCategory) -> Unit) {
    Text("What type of assistance do you need?", fontSize = 17.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(4.dp))
    Text("Select one or more categories.", fontSize = 13.sp, color = LabelGray)
    Spacer(modifier = Modifier.height(16.dp))

    OptionGridMulti(
        options = AssistanceCategory.entries,
        selected = selected,
        onToggle = onToggle,
        label = { it.label },
        icon = { it.icon }
    )
}

@Composable
private fun <T> OptionGridMulti(
    options: List<T>,
    selected: Set<T>,
    onToggle: (T) -> Unit,
    label: (T) -> String,
    icon: (T) -> ImageVector
) {
    options.chunked(2).forEach { rowItems ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            rowItems.forEach { option ->
                OptionCardMulti(
                    label = label(option),
                    icon = icon(option),
                    isSelected = selected.contains(option),
                    onClick = { onToggle(option) },
                    modifier = Modifier.weight(1f)
                )
            }
            if (rowItems.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun OptionCardMulti(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AccentBlue else BorderGray,
                shape = RoundedCornerShape(14.dp)
            )
            .background(
                color = if (isSelected) AppColors.accentSoft else AppColors.surface,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) AccentBlue else LabelGray,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

// A reusable 2-column grid of selectable icon cards. Works for both
// DisasterType and AssistanceCategory since both have a label + icon.
@Composable
private fun <T> OptionGrid(
    options: List<T>,
    selected: T?,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    icon: (T) -> ImageVector
) {
    options.chunked(2).forEach { rowItems ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            rowItems.forEach { option ->
                OptionCard(
                    label = label(option),
                    icon = icon(option),
                    isSelected = option == selected,
                    onClick = { onSelect(option) },
                    modifier = Modifier.weight(1f)
                )
            }
            if (rowItems.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun OptionCard(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AccentBlue else BorderGray,
                shape = RoundedCornerShape(14.dp)
            )
            .background(
                color = if (isSelected) AppColors.accentSoft else AppColors.surface,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) AccentBlue else LabelGray,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

// ---------- Step 3: "Provide details about your need" ----------
@Composable
private fun DetailsStep(
    itemNeeded: String,
    onItemNeededChange: (String) -> Unit,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    unit: String,
    onUnitChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    affectedPersons: String,
    onAffectedPersonsChange: (String) -> Unit,
    evidenceUri: Uri?,
    onUploadImage: () -> Unit,
    onImageRemoved: () -> Unit
) {
    Text("Provide details about your need", fontSize = 17.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))

    FieldLabel("ITEM NEEDED")
    OutlinedTextField(
        value = itemNeeded,
        onValueChange = onItemNeededChange,
        placeholder = { Text("e.g. Rice (5kg sacks)") },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = fieldColors(),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            FieldLabel("QUANTITY")
            OutlinedTextField(
                value = quantity,
                onValueChange = onQuantityChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            FieldLabel("UNIT")
            UnitDropdown(selectedUnit = unit, onUnitSelected = onUnitChange)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    FieldLabel("DESCRIPTION")
    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        placeholder = { Text("Additional context about your need...") },
        shape = RoundedCornerShape(14.dp),
        colors = fieldColors(),
        // Allow the text field to expand vertically as the user types
        // so the entered text is always visible.
        minLines = 3,
        maxLines = 8,
        modifier = Modifier
            .fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    FieldLabel("NUMBER OF AFFECTED PERSONS")
    val affectedCount = affectedPersons.toIntOrNull()
    OutlinedTextField(
        value = affectedPersons,
        onValueChange = onAffectedPersonsChange,
        singleLine = true,
        isError = affectedPersons.isNotBlank() && (affectedCount == null || affectedCount < 1),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(14.dp),
        colors = fieldColors(),
        modifier = Modifier.fillMaxWidth()
    )
    if (affectedPersons.isNotBlank() && (affectedCount == null || affectedCount < 1)) {
        Text(
            text = "Please enter a valid number of affected persons (at least 1).",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFD64550)
        )
    } else {
        Text("Enter the number of affected persons.", fontSize = 11.sp, color = LabelGray)
    }

    Spacer(modifier = Modifier.height(16.dp))

    FieldLabel("UPLOAD IMAGE")

    if (evidenceUri == null) {
        // No photo yet: show the dashed "tap to upload" box (same as the design).
        UploadImageBox(onClick = onUploadImage)
        Spacer(modifier = Modifier.height(8.dp))
        Text("JPG or PNG, up to 5 MB.", fontSize = 11.sp, color = LabelGray)
    } else {
        // A photo was picked: show a preview plus Replace / Remove actions.
        val evidenceBitmap = rememberEvidenceBitmap(evidenceUri)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(AppColors.cardGray, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (evidenceBitmap != null) {
                Image(
                    bitmap = evidenceBitmap,
                    contentDescription = "Selected evidence photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "Preview unavailable",
                    fontSize = 13.sp,
                    color = LabelGray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Image attached. You can replace or remove it before submitting.",
            fontSize = 12.sp,
            color = AppColors.textSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onUploadImage,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Replace Image", fontWeight = FontWeight.SemiBold, color = AccentBlue)
            }
            Button(
                onClick = onImageRemoved,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDECEE)),
                modifier = Modifier.weight(1f)
            ) {
                Text("Remove Image", fontWeight = FontWeight.SemiBold, color = Color(0xFFD64550))
            }
        }
    }
}

/**
 * The dashed upload box from the design: a cloud icon, a small "+ Upload"
 * button, and a hint that tapping anywhere opens the phone's photo picker.
 */
@Composable
private fun UploadImageBox(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .dashedBorder(color = BorderGray, cornerRadius = 12.dp)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CloudUpload,
            contentDescription = null,
            tint = LabelGray,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .background(AccentBlue, RoundedCornerShape(20.dp))
                .clickable { onClick() }
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                "+ Upload",
                color = if (AppColors.isDark) Color(0xFF101018) else Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap to select a photo from your device",
            fontSize = 11.sp,
            color = LabelGray
        )
    }
}

/** Draws a dashed rounded border, used by the upload box above. */
private fun Modifier.dashedBorder(color: Color, cornerRadius: Dp): Modifier = this.drawBehind {
    drawRoundRect(
        color = color,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx()),
        style = Stroke(
            width = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
        )
    )
}

// Loads the selected evidence photo off the main thread and turns it into an
// ImageBitmap that Compose's Image composable can render directly.
@Composable
private fun rememberEvidenceBitmap(uri: Uri): ImageBitmap? {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(initialValue = null, key1 = uri) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }
    return bitmap
}

@Composable
private fun FieldLabel(text: String) {
    Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = LabelGray)
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = FieldBackground,
    focusedContainerColor = FieldBackground,
    unfocusedBorderColor = Color.Transparent,
    focusedBorderColor = AccentBlue
)

// A simple dropdown for picking a unit (packs, kg, liters, etc).
@Composable
private fun UnitDropdown(selectedUnit: String, onUnitSelected: (String) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(FieldBackground, RoundedCornerShape(14.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { isExpanded = true }
                )
                .padding(horizontal = 14.dp)
        ) {
            Text(text = selectedUnit, fontSize = 14.sp, color = TextPrimary)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = LabelGray)
        }

        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            unitOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onUnitSelected(option)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

// ---------- Step 4: "Review Your Request" ----------
@Composable
private fun ReviewStep(
    disaster: DisasterType?,
    categories: Set<AssistanceCategory>,
    itemNeeded: String,
    quantity: String,
    unit: String,
    affectedPersons: String,
    address: String,
    contact: String
) {
    Text("Review Your Request", fontSize = 17.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(4.dp))
    Text("Please review before submitting.", fontSize = 13.sp, color = LabelGray)
    Spacer(modifier = Modifier.height(16.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderGray, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        ReviewRow("Disaster Type", disaster?.label ?: "-")
        ReviewRow("Categories", if (categories.isEmpty()) "-" else categories.joinToString(", ") { it.label })
        ReviewRow("Item Needed", itemNeeded)
        ReviewRow("Quantity", "$quantity $unit")
        ReviewRow("Persons Affected", affectedPersons)
        ReviewRow("Address", if (address.isBlank()) "-" else address)
        ReviewRow("Contact", if (contact.isBlank()) "-" else contact, showDivider = false)
    }

    Spacer(modifier = Modifier.height(14.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.accentSoft, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Icon(Icons.Default.Info, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Your request will be reviewed by an administrator before being posted to the community needs board.",
            fontSize = 12.sp,
            color = AccentBlue
        )
    }
}

@Composable
private fun ReviewRow(label: String, value: String, showDivider: Boolean = true) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Text(text = label, fontSize = 13.sp, color = LabelGray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
    if (showDivider) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderGray)
        )
    }
}

// ---------- The step tracker at the top: Disaster -> Needs -> Details -> Review ----------
@Composable
private fun WizardStepTracker(currentStep: WizardStep) {
    val steps = WizardStep.entries.filter { it != WizardStep.SUCCESS }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        steps.forEachIndexed { index, step ->
            val isDone = step.stepNumber < currentStep.stepNumber
            val isCurrent = step == currentStep
            val circleColor = when {
                isDone -> DoneGreen
                isCurrent -> AccentBlue
                else -> BorderGray
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    if (index != 0) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (isDone || isCurrent) DoneGreen else BorderGray)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Box(
                        modifier = Modifier
                            .size(26.dp)
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
                                    text = step.stepNumber.toString(),
                                    color = if (isCurrent && AppColors.isDark) Color(0xFF101018) else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    if (index != steps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (isDone) DoneGreen else BorderGray)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.label,
                    fontSize = 10.sp,
                    color = if (isDone) DoneGreen else if (isCurrent) AccentBlue else LabelGray
                )
            }
        }
    }
}

// ---------- Success screen shown after submitting ----------
@Composable
private fun RequestSubmittedScreen(
    onViewMyRequest: () -> Unit,
    onBackToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFDFF3E3)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = DoneGreen,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Request Submitted!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.surface, RoundedCornerShape(14.dp))
                .padding(vertical = 14.dp)
        ) {
            Text("Request ID", fontSize = 12.sp, color = LabelGray)
            Spacer(modifier = Modifier.height(4.dp))
            Text("#BL-000301", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AccentBlue)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your request is now pending administrator verification. You will be notified once it has been reviewed.",
            fontSize = 13.sp,
            color = LabelGray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onViewMyRequest,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text("View My Request", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Back to Home", color = AccentBlue, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Physical distribution of assistance is coordinated and verified by the appropriate government authorities.",
            fontSize = 11.sp,
            color = LabelGray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}