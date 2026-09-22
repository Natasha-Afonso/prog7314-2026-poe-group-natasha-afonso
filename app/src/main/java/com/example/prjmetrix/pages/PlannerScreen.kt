package com.example.prjmetrix.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prjmetrix.CalendarEventDto
import com.example.prjmetrix.CalendarRepository
import com.example.prjmetrix.ClothingViewFact
import com.example.prjmetrix.ClothingViewModel
import com.example.prjmetrix.OutfitDto
import com.example.prjmetrix.OutfitRepository
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import com.example.prjmetrix.ClothingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    onBackClick: () -> Unit
) {

    val context = LocalContext.current

    val clothingVm: ClothingViewModel = viewModel(
        factory = ClothingViewFact(context)
    )

    val clothingState by clothingVm.uiState.collectAsState()

    val outfitRepository =
        remember {
            OutfitRepository(context)
        }

    var month by remember {
        mutableStateOf(
            YearMonth.now()
        )
    }

    var selectedDate by remember {
        mutableStateOf(
            LocalDate.now()
        )
    }

    var events by remember {
        mutableStateOf(
            emptyList<CalendarEventDto>()
        )
    }

    var outfits by remember {
        mutableStateOf(
            emptyList<OutfitDto>()
        )
    }

    var showCreateDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(month) {

        clothingVm.load()

        val outfitResult =
            outfitRepository.getOutfits()

        if (outfitResult.isSuccess) {
            outfits =
                outfitResult.getOrNull()
                    ?: emptyList()
        }

        val calendarResult =
            CalendarRepository(context)
                .month(
                    month.year,
                    month.monthValue
                )

        if (calendarResult.isSuccess) {
            events =
                calendarResult.getOrNull()
                    ?: emptyList()
        }
    }

    Scaffold(

        topBar = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 8.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.ArrowBack,
                        contentDescription =
                            "Back"
                    )
                }

                Text(
                    text = "Planner",
                    style =
                        MaterialTheme.typography.headlineSmall
                )
            }
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                IconButton(
                    onClick = {
                        month =
                            month.minusMonths(1)
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.ChevronLeft,
                        contentDescription =
                            "Previous month"
                    )
                }

                Text(
                    text =
                        "${month.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${month.year}",
                    style =
                        MaterialTheme.typography.titleLarge
                )

                IconButton(
                    onClick = {
                        month =
                            month.plusMonths(1)
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.ChevronRight,
                        contentDescription =
                            "Next month"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                listOf(
                    "Sun",
                    "Mon",
                    "Tue",
                    "Wed",
                    "Thu",
                    "Fri",
                    "Sat"
                ).forEach { day ->

                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        style =
                            MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            val firstDay =
                month.atDay(1)

            val firstOffset =
                firstDay.dayOfWeek.value % 7

            val days: List<LocalDate?> =
                List(firstOffset) {
                    null
                } +
                        (1..month.lengthOfMonth()).map { day ->
                            month.atDay(day)
                        }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp),
                horizontalArrangement =
                    Arrangement.spacedBy(4.dp),
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {

                items(
                    items = days
                ) { value ->

                    if (value == null) {

                        Spacer(
                            modifier = Modifier.height(45.dp)
                        )

                    } else {

                        val date =
                            value as LocalDate

                        val dayEvents =
                            events.filter {
                                it.date == date.toString()
                            }

                        Card(
                            modifier = Modifier
                                .height(45.dp)
                                .fillMaxWidth()
                        ) {

                            Column(
                                modifier =
                                    Modifier.padding(4.dp),
                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text =
                                        date.dayOfMonth.toString(),
                                    style =
                                        MaterialTheme.typography.labelSmall
                                )

                                if (dayEvents.isNotEmpty()) {

                                    Text(
                                        text =
                                            when (
                                                dayEvents.first().type
                                            ) {
                                                "Birthday" -> "🎂"
                                                "Holiday" -> "🎉"
                                                else -> "👗"
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = {
                    showCreateDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    "Create Outfit"
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text =
                    "Scheduled events",
                style =
                    MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            val selectedEvents =
                events.filter {
                    it.date == selectedDate.toString()
                }

            if (selectedEvents.isEmpty()) {

                Text(
                    "No events on ${selectedDate}."
                )

            } else {

                selectedEvents.forEach { event ->

                    Text(
                        text =
                            when (event.type) {
                                "Birthday" ->
                                    "🎂 ${event.title}"

                                "Holiday" ->
                                    "🎉 ${event.title}"

                                else ->
                                    "👗 ${event.title}"
                            },
                        modifier =
                            Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text =
                    "Wardrobe items: ${clothingState.clothing.size}",
                style =
                    MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (showCreateDialog) {

        CreateOutfitDialog(
            clothing = clothingState.clothing,
            selectedDate = selectedDate,
            onDismiss = {
                showCreateDialog = false
            },
            onCreated = {

                showCreateDialog = false

                // Reload planner data
                clothingVm.load()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateOutfitDialog(
    clothing: List<ClothingItem>,
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onCreated: () -> Unit
) {

    val context = LocalContext.current

    val repository =
        remember {
            OutfitRepository(context)
        }

    var outfitName by remember {
        mutableStateOf("")
    }

    var selectedItems by remember {
        mutableStateOf(
            emptySet<Int>()
        )
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Create Outfit")
        },

        text = {

            Column {

                OutlinedTextField(
                    value = outfitName,
                    onValueChange = {
                        outfitName = it
                    },
                    label = {
                        Text("Outfit name")
                    },
                    singleLine = true,
                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {

                    OutlinedTextField(
                        value =
                            if (selectedItems.isEmpty())
                                "Select clothing"
                            else
                                "${selectedItems.size} selected",

                        onValueChange = {},

                        readOnly = true,

                        label = {
                            Text("Clothing")
                        },

                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {

                        clothing.forEach { item ->

                            DropdownMenuItem(

                                text = {
                                    Text(
                                        "${item.name} (${item.category})"
                                    )
                                },

                                onClick = {

                                    selectedItems =
                                        if (
                                            selectedItems.contains(
                                                item.id
                                            )
                                        ) {
                                            selectedItems -
                                                    item.id
                                        } else {
                                            selectedItems +
                                                    item.id
                                        }
                                }
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    "Date: $selectedDate"
                )
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    if (outfitName.isBlank()) {
                        return@Button
                    }

                    if (selectedItems.isEmpty()) {
                        return@Button
                    }

                    // This section depends on your existing
                    // OutfitRepository signatures.
                    // It creates the outfit and schedules it.

                    kotlinx.coroutines.CoroutineScope(
                        kotlinx.coroutines.Dispatchers.Main
                    ).launch {

                        val result =
                            repository.create(
                                outfitName.trim(),
                                selectedItems.toList()
                            )

                        if (result.isSuccess) {

                            val outfit =
                                result.getOrNull()

                            if (outfit != null) {

                                val scheduleResult =
                                    repository.schedule(
                                        outfit.id,
                                        selectedDate.toString()
                                    )

                                if (scheduleResult.isSuccess) {
                                    onCreated()
                                }
                            }
                        }
                    }
                }
            ) {
                Text("Create")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}