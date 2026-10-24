package com.example.prjmetrix.pages



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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prjmetrix.ClothingViewFact
import com.example.prjmetrix.ClothingViewModel
import com.example.prjmetrix.Outfit
import com.example.prjmetrix.ProfileUiState
import com.example.prjmetrix.ProfileViewModel
import com.example.prjmetrix.ProfileViewModelFactory
import com.example.prjmetrix.StyleViewModel
import com.example.prjmetrix.isWeatherPick
import com.example.prjmetrix.StyleViewModelFactory
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

//In app calendar plan outfits for a day, and see the user's birthday and national holidays
@Composable
fun PlannerScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val styleViewModel: StyleViewModel = viewModel(factory = StyleViewModelFactory(context))
    val clothingViewModel: ClothingViewModel = viewModel(factory = ClothingViewFact(context))
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(context))

    val style by styleViewModel.uiState.collectAsState()
    val clothing by clothingViewModel.uiState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()

    //Only trust the birthday once the real profile has loaded
    val birthday: LocalDate? = (profileState as? ProfileUiState.Ready)?.profile?.birthday

    var month by remember { mutableStateOf(YearMonth.now()) }
    var selected by remember { mutableStateOf(LocalDate.now()) }
    var showBuilder by remember { mutableStateOf(false) }
    var showPickSaved by remember { mutableStateOf(false) }

    val backgroundColor = Color(0xFFC1DBE8)
    val darkText = Color(0xFF43302E)
    val accent = Color(0xFF5A3A35)

    LaunchedEffect(Unit) {
        styleViewModel.loadOutfits()
        clothingViewModel.loadClothing()
    }
    LaunchedEffect(month.year) { styleViewModel.loadHolidays(month.year) }
    LaunchedEffect(month) { styleViewModel.loadSchedule(month.atDay(1), month.atEndOfMonth()) }

    fun isBirthday(date: LocalDate): Boolean {
        if (birthday == null) return false
        if (birthday.monthValue == 2 && birthday.dayOfMonth == 29 && !date.isLeapYear) {
            return date.monthValue == 2 && date.dayOfMonth == 28
        }
        return date.monthValue == birthday.monthValue && date.dayOfMonth == birthday.dayOfMonth
    }


    fun outfitFor(date: LocalDate): Outfit? =
        style.schedule[date.toString()]?.let { id -> style.outfits.firstOrNull { it.id == id } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "‹", fontSize = 38.sp, color = darkText,
                    modifier = Modifier.clickable { onBackClick() }.padding(end = 8.dp)
                )
                Text("Outfit Calendar", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = darkText)
            }
            Button(
                onClick = {
                    month = YearMonth.now()
                    selected = LocalDate.now()
                },
                shape = RoundedCornerShape(20.dp)
            ) { Text("Today", fontSize = 14.sp) }
        }

        Spacer(Modifier.height(20.dp))

        //Month grid
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "‹", fontSize = 30.sp, color = darkText,
                        modifier = Modifier.clickable { month = month.minusMonths(1) }.padding(horizontal = 12.dp)
                    )
                    Text(
                        selected.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")),
                        fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkText
                    )
                    Text(
                        "›", fontSize = 30.sp, color = darkText,
                        modifier = Modifier.clickable { month = month.plusMonths(1) }.padding(horizontal = 12.dp)
                    )
                }

                Spacer(Modifier.height(14.dp))

                Row(Modifier.fillMaxWidth()) {
                    listOf("S", "M", "T", "W", "T", "F", "S").forEach { d ->
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(d, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))

                // Sunday-first grid
                val leading = month.atDay(1).dayOfWeek.value % 7
                val cells: List<LocalDate?> =
                    List(leading) { null } + (1..month.lengthOfMonth()).map { month.atDay(it) }

                cells.chunked(7).forEach { week ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                        week.forEach { date ->
                            DayCell(
                                date = date,
                                isSelected = date == selected,
                                isToday = date == LocalDate.now(),
                                hasOutfit = date != null && style.schedule.containsKey(date.toString()),
                                hasHoliday = date != null && style.holidays.containsKey(date.toString()),
                                hasBirthday = date != null && isBirthday(date),
                                accent = accent,
                                darkText = darkText,
                                modifier = Modifier.weight(1f),
                                onClick = { if (date != null) selected = date }
                            )
                        }
                        repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text("🎂 birthday   🎉 holiday   👗 outfit planned", fontSize = 11.sp, color = Color.Gray)
            }
        }

        Spacer(Modifier.height(22.dp))

        // Selected day
        Text(
            month.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = darkText
        )
        Spacer(Modifier.height(10.dp))

        val holidays = style.holidays[selected.toString()].orEmpty()
        if (isBirthday(selected)) {
            InfoCard("🎂", "Happy birthday!", "Your special day", Color(0xFFFFE8E2), darkText)
            Spacer(Modifier.height(10.dp))
        }
        holidays.forEach { h ->
            InfoCard("🎉", h.localName.ifBlank { h.name }, "National holiday", Color(0xFFFFF1B5), darkText)
            Spacer(Modifier.height(10.dp))
        }

        val dayOutfit = outfitFor(selected)
        if (dayOutfit == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("No outfit planned for this day. Only one outfit can be planned per day.", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            PlannedOutfitCard(
                outfit = dayOutfit,
                darkText = darkText,
                onRemove = { styleViewModel.unschedule(selected) },
                onDelete = { styleViewModel.deleteOutfit(dayOutfit.id) }
            )
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { showBuilder = true }, modifier = Modifier.weight(1f)) {
                Text("Create outfit")
            }
            OutlinedButton(
                onClick = { showPickSaved = true },
                enabled = style.outfits.any { !it.isWeatherPick },
                modifier = Modifier.weight(1f)
            ) { Text("Use saved outfit") }
        }

        style.message?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, fontSize = 13.sp, color = accent)
        }

        Spacer(Modifier.height(30.dp))
    }

    if (showBuilder) {
        OutfitBuilderDialog(
            clothing = clothing.clothing,
            initialDate = selected,
            onDismiss = { showBuilder = false },
            onSave = { name, pieces, date ->
                styleViewModel.createOutfit(name, pieces, date)
                showBuilder = false
            }
        )
    }

    if (showPickSaved) {
        AlertDialog(
            onDismissRequest = { showPickSaved = false },
            title = { Text("Plan which outfit for $selected?") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    style.outfits
                        .filterNot { it.isWeatherPick }
                        .forEach { outfit ->
                            TextButton(onClick = {
                                styleViewModel.scheduleOutfit(outfit.id, selected)
                                showPickSaved = false
                            }) { Text(outfit.name) }
                        }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showPickSaved = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun DayCell(
    date: LocalDate?,
    isSelected: Boolean,
    isToday: Boolean,
    hasOutfit: Boolean,
    hasHoliday: Boolean,
    hasBirthday: Boolean,
    accent: Color,
    darkText: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    if (date == null) {
        Spacer(modifier.height(46.dp))
        return
    }
    Column(
        modifier = modifier
            .height(46.dp)
            .padding(1.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> accent
                    isToday -> Color(0xFFF2DCD6)
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            date.dayOfMonth.toString(),
            fontSize = 13.sp,
            color = if (isSelected) Color.White else darkText,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
        )
        val marks = buildString {
            if (hasBirthday) append("🎂")
            if (hasHoliday) append("🎉")
            if (hasOutfit) append("👗")
        }
        if (marks.isNotEmpty()) Text(marks, fontSize = 7.sp)
    }
}

@Composable
private fun InfoCard(emoji: String, title: String, subtitle: String, tint: Color, darkText: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = tint)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 26.sp)
            Column(Modifier.padding(start = 12.dp)) {
                Text(title, fontWeight = FontWeight.Bold, color = darkText)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun PlannedOutfitCard(
    outfit: Outfit,
    darkText: Color,
    onRemove: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(outfit.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkText)
            Spacer(Modifier.height(8.dp))
            PieceRow(outfit.pieces, size = 72)
            Spacer(Modifier.height(4.dp))
            Row {
                TextButton(onClick = onRemove) { Text("Unplan") }
                TextButton(onClick = onDelete) { Text("Delete outfit") }
            }
        }
    }
}
