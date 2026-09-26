package com.example.bayanihanlink

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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AccentBlue = Color(0xFF2B49CC)
private val LabelGray = Color(0xFF8A8FA3)
private val BorderGray = Color(0xFFE3E5EC)
private val DoneGreen = Color(0xFF3BB273)

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
private enum class WizardStep(val stepNumber: Int, val label: String) {
    DISASTER(1, "Disaster"),
    NEEDS(2, "Needs"),
    DETAILS(3, "Details"),
    REVIEW(4, "Review"),
    SUCCESS(5, "") // not shown in the step tracker
}

private val unitOptions = listOf("packs", "kg", "liters", "pieces", "sets", "boxes", "bottles")

@Composable
fun RequestAssistanceScreen(
    onExit: () -> Unit = {},
    onViewMyRequest: () -> Unit = {},
    onBackToHome: () -> Unit = {}
) {
    // Which step of the wizard we're currently showing.
    var currentStep by remember { mutableStateOf(WizardStep.DISASTER) }

    // Everything the user picks/types, kept in one place.
    var selectedDisaster by remember { mutableStateOf<DisasterType?>(null) }
    var selectedCategory by remember { mutableStateOf<AssistanceCategory?>(null) }
    var itemNeeded by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf(unitOptions.first()) }
    var description by remember { mutableStateOf("") }
    var affectedPersons by remember { mutableStateOf("") }

    // The Next button on each step is only enabled once the required info is filled in.
    val isDisasterStepValid = selectedDisaster != null
    val isNeedsStepValid = selectedCategory != null
    val isDetailsStepValid = itemNeeded.isNotBlank() && quantity.isNotBlank() && affectedPersons.isNotBlank()

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
            .background(Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
        ) {
            IconButton(onClick = onExit) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Exit", tint = Color(0xFF1A1A2E))
            }
            Text("Request Assistance", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
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
                    selected = selectedCategory,
                    onSelect = { selectedCategory = it }
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
                    onAffectedPersonsChange = { affectedPersons = it }
                )
                WizardStep.REVIEW -> ReviewStep(
                    disaster = selectedDisaster,
                    category = selectedCategory,
                    itemNeeded = itemNeeded,
                    quantity = quantity,
                    unit = unit,
                    affectedPersons = affectedPersons
                )
                WizardStep.SUCCESS -> Unit // handled above, never reached here
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
                WizardStep.REVIEW -> true // review step has nothing left to fill in
                WizardStep.SUCCESS -> true
            }

            Button(
                onClick = {
                    if (currentStep == WizardStep.REVIEW) {
                        // TODO: actually send the request to your backend/database here later.
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
private fun NeedsStep(selected: AssistanceCategory?, onSelect: (AssistanceCategory) -> Unit) {
    Text("What type of assistance do you need?", fontSize = 17.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(4.dp))
    Text("Select the category that best matches your need.", fontSize = 13.sp, color = LabelGray)
    Spacer(modifier = Modifier.height(16.dp))

    OptionGrid(
        options = AssistanceCategory.entries,
        selected = selected,
        onSelect = onSelect,
        label = { it.label },
        icon = { it.icon }
    )
}
@Composable
private fun <T> OptionGrid(
    options: List<T>,
    selected: T?,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    icon: (T) -> ImageVector
) {
    // Split the list into rows of 2, then draw each row as a Row of 2 cards.
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
                color = if (isSelected) Color(0xFFEEF0FC) else Color.White,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) AccentBlue else LabelGray,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A2E))
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
    onAffectedPersonsChange: (String) -> Unit
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
        placeholder = { Text("To donate..") },
        shape = RoundedCornerShape(14.dp),
        colors = fieldColors(),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    FieldLabel("NUMBER OF AFFECTED PERSONS")
    OutlinedTextField(
        value = affectedPersons,
        onValueChange = onAffectedPersonsChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(14.dp),
        colors = fieldColors(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun FieldLabel(text: String) {
    Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = LabelGray)
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = Color(0xFFF7F8FA),
    focusedContainerColor = Color(0xFFF7F8FA),
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
                .background(Color(0xFFF7F8FA), RoundedCornerShape(14.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { isExpanded = true }
                )
                .padding(horizontal = 14.dp)
        ) {
            Text(text = selectedUnit, fontSize = 14.sp, color = Color(0xFF1A1A2E))
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
    category: AssistanceCategory?,
    itemNeeded: String,
    quantity: String,
    unit: String,
    affectedPersons: String
) {
    val address = "Brgy. 14, Cagayan de Oro City"
    val contact = "+63 912 345 6789"

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
        ReviewRow("Category", category?.label ?: "-")
        ReviewRow("Item Needed", itemNeeded)
        ReviewRow("Quantity", "$quantity $unit")
        ReviewRow("Persons Affected", affectedPersons)
        ReviewRow("Address", address)
        ReviewRow("Contact", contact, showDivider = false)
    }

    Spacer(modifier = Modifier.height(14.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEEF0FC), RoundedCornerShape(14.dp))
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
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A2E))
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
                else -> Color(0xFFE3E5EC)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    if (index != 0) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (isDone || isCurrent) DoneGreen else Color(0xFFE3E5EC))
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
                            Text(
                                text = step.stepNumber.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (index != steps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (isDone) DoneGreen else Color(0xFFE3E5EC))
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
            .background(Color(0xFFF7F8FC))
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

        Text("Request Submitted!", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(14.dp))
                .padding(vertical = 14.dp)
        ) {
            Text("Request ID", fontSize = 12.sp, color = LabelGray)
            Spacer(modifier = Modifier.height(4.dp))
            // TODO: replace with the real generated request ID once you have a backend.
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