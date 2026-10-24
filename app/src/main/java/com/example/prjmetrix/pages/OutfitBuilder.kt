package com.example.prjmetrix.pages



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.prjmetrix.ClothingItem
import com.example.prjmetrix.OutfitPiece
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

fun ClothingItem.toPiece() = OutfitPiece(id, name, category, imageUrl)


// Used from both the Wardrobe and the Calendar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitBuilderDialog(
    clothing: List<ClothingItem>,
    initialDate: LocalDate? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, pieces: List<OutfitPiece>, date: LocalDate?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val selected = remember { mutableStateListOf<Int>() }
    var schedule by remember { mutableStateOf(initialDate != null) }
    var date by remember { mutableStateOf(initialDate ?: LocalDate.now()) }
    var showPicker by remember { mutableStateOf(false) }

    val categories = listOf("Tops", "Bottoms", "Dresses", "Jackets", "Shoes", "Accessories")
    val canSave = selected.isNotEmpty()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text("Create an outfit", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(40) },
                    label = { Text("Outfit name (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Plan it for a day", fontWeight = FontWeight.Medium)
                        if (schedule) {
                            Text(date.toString(), fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                    if (schedule) {
                        TextButton(onClick = { showPicker = true }) { Text("Change date") }
                    }
                    Switch(checked = schedule, onCheckedChange = { schedule = it })
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Pick pieces (${selected.size} selected)",
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))

                if (clothing.isEmpty()) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Add some clothing to your wardrobe first.")
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        categories.forEach { category ->
                            val items = clothing.filter { it.category == category }
                            if (items.isNotEmpty()) {
                                Text(category, fontSize = 13.sp, color = Color.Gray)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(items, key = { it.id }) { item ->
                                        val isSelected = item.id in selected
                                        Card(
                                            modifier = Modifier
                                                .width(96.dp)
                                                .clickable {
                                                    if (isSelected) selected.remove(item.id) else selected.add(item.id)
                                                },
                                            border = if (isSelected)
                                                BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null,
                                            colors = CardDefaults.cardColors(containerColor = Color.White)
                                        ) {
                                            ClothingThumb(item.imageUrl, item.name, Modifier.size(96.dp))
                                            Text(
                                                item.name,
                                                fontSize = 11.sp,
                                                maxLines = 1,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        enabled = canSave,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val pieces = clothing.filter { it.id in selected }.map { it.toPiece() }
                            val finalName = name.ifBlank { "Outfit ${LocalDate.now()}" }
                            onSave(finalName, pieces, if (schedule) date else null)
                        }
                    ) { Text(if (schedule) "Save & plan" else "Save outfit") }
                }
            }
        }
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let {
                        date = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = pickerState) }
    }
}

//Clothing picture
@Composable
fun ClothingThumb(imageUrl: String?, description: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2DCD6)),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = description,
                modifier = Modifier.fillMaxSize().padding(4.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Text("👗", fontSize = 26.sp)
        }
    }
}
