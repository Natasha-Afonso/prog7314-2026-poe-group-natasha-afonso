package com.example.prjmetrix.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prjmetrix.JournalResponse
import com.example.prjmetrix.JournalViewModel
import com.example.prjmetrix.JournalView2
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeParseException

//(StackOverflow, 2023)
private fun formatJournalDate(date: String): String {
    return try {
        val instant = try { Instant.parse(date)}
        catch(e: DateTimeParseException) { //takes the current date
            LocalDateTime.parse(date).toInstant(ZoneOffset.UTC)
        }
        val formatter = DateTimeFormatter //formatting how the date displays
            .ofPattern("yyyy-MM-dd  hh:mma", Locale.getDefault())
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        date
    }
}
//(Singh, 2023)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalList(
    onBack: () -> Unit,
    onWriteEntry: () -> Unit,
    onEditEntry: (Int) -> Unit
) {
    //(Sharma, 2024)
    val context = LocalContext.current
    val viewModel: JournalViewModel = viewModel(factory = JournalView2(context))
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadEntries() }

    Scaffold(
        containerColor = Color(0xFFFAF6EC),
        topBar = {
            TopAppBar(
                title = { Text("My Journal", fontWeight = FontWeight.Bold, color = Color(0xFF7A3E3E)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint =Color(0xFF7A3E3E))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFB9D7E8))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onWriteEntry,
                containerColor = Color(0xFF7A3E3E),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Write entry")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading && state.entries.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF5B2A2A)
                    )
                }
                state.entries.isEmpty() -> {
                    Text(
                        text = "No entries yet — tap + to write your first one.",
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        color = Color(0xFF5B2A2A)
                    )
                }
                //(Krishna, 2025.)
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.entries, key = { it.journalId }) { entry ->
                            JournalEntryCard(
                                entry = entry,
                                onClick = { onEditEntry(entry.journalId) },
                                onDelete = { viewModel.deleteEntry(entry.journalId) }
                            )
                        }
                    }
                }
            }

            state.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun JournalEntryCard(
    entry: JournalResponse,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E4A8)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFB9D7E8)),
                contentAlignment = Alignment.Center
            ) {
                Text(entry.mood, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatJournalDate(entry.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF5B2A2A)
                )
                Text(
                    text = entry.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF3A2A2A),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete entry", tint = Color(0xFF5B2A2A))
            }
        }
    }
}
/*References

* Android Developers, 2026. Jetpack Compose Tutorial. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/develop/ui/compose/tutorial > [Accessed 17 September 2026]
* Kostadinov, D., 2025. Top 3 Most common animations you can use in your jetpack compose project. (Version 10.0) [Source Code]. Available at: < https://proandroiddev.com/top-3-most-common-animations-you-can-use-in-your-jetpack-compose-project-9bb92f5311a2 > [Accessed 22 September 2026]
* Jovanovic, S., 2024. Diary App. (Version 10.0) [Source Code]. Available at: < https://github.com/stevdza-san/DiaryApp/tree/master > [Accessed 20 September 2026]
* Krishna, M., 2025. Personal Diary App. (Version 10.0) [Source Code]. Available at: < https://github.com/Murali-KrishnaM/PersonalDiaryApp/blob/main/app/src/main/java/com/example/diaryapp/AddEntryActivity.kt > [Accessed 20 September 2026]
* Alpasian, E., 2023. DiaryApp. (Version 10.0) [Source code]. Available at: < https://github.com/erenalpaslan/DiaryApp > [Accessed 19 September 2026]
* Sharma, S., 2024. Mood-Journal. (Version 10.0) [Source code]. Available at: < https://github.com/SakshamSharma2026/Mood-Journal > [Accessed 21 September 2026]
* StackOverflow, 2023. Jetpack Compose Format Date String. (Version 10.0) [Source Code]. Available at: < https://stackoverflow.com/questions/75968843/jetpack-compose-format-date-string > [Accessed 20 September 2026]

 */