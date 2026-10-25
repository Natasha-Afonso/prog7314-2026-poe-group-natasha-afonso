package com.example.prjmetrix

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.Image

// Colours used by the dialogs.
private val DialogInk = Color(0xFF43302E)
private val DialogInkSoft = Color(0xFF8D6E63)
private val DialogBorder = Color(0xFFE7B4BE)

// Dialog for creating a new habit.
@Composable
fun NewHabitDialog(
    onDismiss: () -> Unit,
    onCreate: (label: String, targetCount: Int, iconKey: String, colorHex: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var target by remember { mutableIntStateOf(1) }
    var iconKey by remember { mutableStateOf(HabitIcons.first().key) }
    var colorHex by remember { mutableStateOf(HabitColors.first()) }
    val color = colorFromHex(colorHex)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Previews the selected icon and colour.
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f))
                    .border(2.dp, color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconForKey(iconKey), contentDescription = null, tint = color, modifier = Modifier.size(30.dp))
            }

            Spacer(Modifier.height(10.dp))
            Text("New habit", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = DialogInk)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("e.g. Drink water") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Sets the daily target.
            Spacer(Modifier.height(16.dp))
            SectionLabel("How many times a day?")
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StepperButton(Icons.Default.Remove, enabled = target > 1, color = color) { target-- }
                Text(
                    text = "$target",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DialogInk,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(64.dp)
                )
                StepperButton(Icons.Default.Add, enabled = target < MAX_DAILY_TARGET, color = color) { target++ }
            }
            Text(
                text = if (target == 1) "Done once a day" else "Each tap adds 1 of $target",
                fontSize = 11.sp,
                color = DialogInkSoft
            )

            // Lets the user choose an icon.
            Spacer(Modifier.height(16.dp))
            SectionLabel("Icon")
            Spacer(Modifier.height(6.dp))
            HabitIcons.chunked(6).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                    row.forEach { option ->
                        val selected = option.key == iconKey
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (selected) color.copy(alpha = 0.2f) else Color(0xFFF3F4F6))
                                .border(if (selected) 2.dp else 0.dp, if (selected) color else Color.Transparent, CircleShape)
                                .clickable { iconKey = option.key },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(option.icon, contentDescription = option.key, tint = if (selected) color else DialogInkSoft, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            // Lets the user choose a colour.
            Spacer(Modifier.height(16.dp))
            SectionLabel("Colour")
            Spacer(Modifier.height(6.dp))
            HabitColors.chunked(6).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                    row.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(colorFromHex(hex))
                                .clickable { colorHex = hex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (hex == colorHex) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            // Shows the coins earned for reaching the goal.
            Spacer(Modifier.height(14.dp))
            Text(
                text = "🪙 $HALF_GOAL_COINS at half way · 🪙 $GOAL_COINS when the goal is done",
                fontSize = 11.sp,
                color = DialogInkSoft,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(4.dp))
                Button(
                    onClick = { onCreate(name.trim(), target, iconKey, colorHex) },
                    enabled = name.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = color),
                    shape = RoundedCornerShape(50)
                ) { Text("Create", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DialogInk, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun StepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (enabled) color.copy(alpha = 0.18f) else Color(0xFFF3F4F6))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = if (enabled) color else Color(0xFFBDBDBD), modifier = Modifier.size(22.dp))
    }
}

// Popup shown when the user completes three daily goals.
@Composable
fun MysteryGiftPopup(
    result: GiftResult?,
    onOpen: () -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = { if (result != null) onClose() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFFFFF9C4))
                .border(2.dp, Color(0xFFFBC02D), RoundedCornerShape(26.dp))
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (result) {
                null -> {
                    Text("🎁", fontSize = 56.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Mystery gift!", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF57F17))
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "You finished $MYSTERY_GIFT_GOALS goals today.",
                        fontSize = 13.sp,
                        color = Color(0xFF5D4037),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = onOpen,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBC02D)),
                        shape = RoundedCornerShape(50)
                    ) { Text("Open", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp) }
                }
                is GiftResult.Coins -> {
                    Text("🪙", fontSize = 56.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("+${result.amount} coins!", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF57F17))
                    Spacer(Modifier.height(14.dp))
                    GiftDoneButton(onClose)
                }
                is GiftResult.Furniture -> {
                    Image(
                        painter = painterResource(id = result.item.drawableRes),
                        contentDescription = result.item.name,
                        modifier = Modifier.height(110.dp).fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("You got a ${result.item.name}!", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF57F17), textAlign = TextAlign.Center)
                    Text("Find it in Edit mode.", fontSize = 12.sp, color = Color(0xFF5D4037))
                    Spacer(Modifier.height(14.dp))
                    GiftDoneButton(onClose)
                }
            }
        }
    }
}

@Composable
private fun GiftDoneButton(onClose: () -> Unit) {
    Button(
        onClick = onClose,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBC02D)),
        shape = RoundedCornerShape(50)
    ) { Text("Nice!", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp) }
}