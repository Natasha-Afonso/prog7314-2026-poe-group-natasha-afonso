package com.example.prjmetrix.pages


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private const val slightlyDeemphasizedAlpha = 0.87f

// ---------- Text question (nickname) ----------
@Composable
fun TextQuestion(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Your nickname") },
        supportingText = { Text("${value.length}/20") },
        singleLine = true,
        shape = MaterialTheme.shapes.small,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

// ---------- Single choice (personality) ----------
@Composable
fun SingleOptionsSection(
    selected: String,
    options: SurveyOptions.SingleChoice,
    onOptionSelected: (String) -> Unit = {}
) {
    options.options.forEach { (option, emoji) ->
        key(option) {
            RadioRow(
                title = option,
                emoji = emoji,
                selected = option == selected,
                onOptionSelected = { onOptionSelected(option) }
            )
        }
    }
}

@Composable
fun RadioRow(
    title: String,
    emoji: String,
    selected: Boolean,
    onOptionSelected: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onOptionSelected),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = emoji, style = MaterialTheme.typography.titleLarge)
            Text(
                text = title,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )
            RadioButton(
                selected = selected,
                modifier = Modifier.padding(8.dp),
                onClick = null
            )
        }
    }
}

// ---------- Date question (birthday) ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateQuestion(
    date: String,
    birthday: LocalDate?,
    onDatePicked: (LocalDate) -> Unit
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }

    OutlinedButton(
        onClick = { showPicker = true },
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface.copy(slightlyDeemphasizedAlpha)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(0.12f)),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = date,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp),
        )
        Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = "Expand")
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = birthday
                ?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
            yearRange = 1930..LocalDate.now().year,
            selectableDates = object : SelectableDates {
                // only past dates, like Jetsurvey's DateValidatorPointBackward
                override fun isSelectableDate(utcTimeMillis: Long) =
                    utcTimeMillis <= System.currentTimeMillis()
            }
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        onDatePicked(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = pickerState)
        }
    }
}