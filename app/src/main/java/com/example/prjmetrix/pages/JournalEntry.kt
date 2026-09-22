package com.example.prjmetrix.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prjmetrix.JournalViewModel
import com.example.prjmetrix.JournalView2
import com.example.prjmetrix.journalMoods

//(Singh, 2023)
@OptIn(ExperimentalMaterial3Api::class)
//(Android Developers, 2026a)
@Composable
fun JournalEntry(
    entryId: Int?,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: JournalViewModel = viewModel(factory = JournalView2(context))
    val state by viewModel.uiState.collectAsStateWithLifecycle()

//    val existing = remember(entryId, state.entries) {
//        entryId?.let { id -> state.entries.firstOrNull { it.journalId == id } }
//    }

    LaunchedEffect(entryId){
        if(entryId != null){
            viewModel.loadEntry(entryId)
        }
    }
    val existing = state.editingEntry
    val getEntry = entryId != null && existing == null
    var selectedMood by rememberSaveable(existing) { mutableStateOf(existing?.mood) }
    var text by rememberSaveable(existing) { mutableStateOf(existing?.text ?: "") }

    //(Alpasian, 2023)
    Scaffold(
        containerColor = Color(0xFFFAF6EC),
        topBar = {
            TopAppBar(
                title = { Text(if (entryId == null) "New entry" else "Edit entry", color = Color(0xFF5B2A2A)) },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF5B2A2A))
                    }
                },
                actions = {
                    if (selectedMood != null) {
                        TextButton(
                            enabled = text.isNotBlank() && !state.isSaving,
                            onClick = {
                                val mood = selectedMood ?: return@TextButton
                                if (entryId == null) {
                                    viewModel.createEntry(mood, text, onDone)
                                } else {
                                    viewModel.updateEntry(entryId, mood, text, onDone)
                                }
                            }
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF5B2A2A))
                            } else {
                                Text("Save", color = Color(0xFF5B2A2A), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFB9D7E8))
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            //gets the entry the user wants to update to load it with existing text and mood
            if(getEntry){
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF5B2A2A))
            }
            else {
                // user picks the mood
                AnimatedVisibility(
                    visible = selectedMood == null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    MoodPrompt(onMoodChosen = { selectedMood = it })
                }

                //(Android Developers, 2026b)
                // top of journal displays the users chosen mood
                AnimatedVisibility(
                    visible = selectedMood != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        MoodBadge(
                            emoji = selectedMood ?: "",
                            onChangeMood = { selectedMood = null }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            modifier = Modifier.fillMaxSize(),
                            placeholder = { Text("Write what's on your mind…") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF5B2A2A),
                                unfocusedBorderColor = Color(0xFF5B2A2A).copy(alpha = 0.4f)
                            )
                        )
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

//(Alpasian, 2023)
@Composable
private fun MoodPrompt(onMoodChosen: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF5B2A2A),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(journalMoods) { mood ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF3E4A8))
                        .padding(vertical = 12.dp, horizontal = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFB9D7E8))
                            .clickable { onMoodChosen(mood.emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(mood.emoji, style = MaterialTheme.typography.headlineSmall)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(mood.label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF5B2A2A))
                }
            }
        }
    }
}
//(Alpasian, 2023)
@Composable
private fun MoodBadge(emoji: String, onChangeMood: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFF3E4A8))
            .clickable { onChangeMood() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(emoji, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Change mood", style = MaterialTheme.typography.labelSmall, color = Color(0xFF5B2A2A))
    }
}
/*References

* Alpasian, E., 2023. DiaryApp. (Version 10.0) [Source code]. Available at: < https://github.com/erenalpaslan/DiaryApp > [Accessed 19 September 2026]
* Android Developers, 2026a. Jetpack Compose Tutorial. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/develop/ui/compose/tutorial > [Accessed 17 September 2026]
* Android Developers, 2026b. Animation modifier and composable. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/develop/ui/compose/animation/composables-modifiers > [Accessed 20 September 2026]
* Jovanovic, S., 2024. Diary App. (Version 10.0) [Source Code]. Available at: < https://github.com/stevdza-san/DiaryApp/tree/master > [Accessed 20 September 2026]
* Krishna, M., 2025. Personal Diary App. (Version 10.0) [Source Code]. Available at: < https://github.com/Murali-KrishnaM/PersonalDiaryApp/blob/main/app/src/main/java/com/example/diaryapp/AddEntryActivity.kt > [Accessed 20 September 2026]
* Sharma, S., 2024. Mood-Journal. (Version 10.0) [Source code]. Available at: < https://github.com/SakshamSharma2026/Mood-Journal > [Accessed 21 September 2026]
* Singh, G., 2023, Material Design 3 for compose gets new components and features. (Version 10.0) [Source Code]. Available at: < https://m3.material.io/blog/material-3-compose-1-1 > [Accessed 20 September 2026]
 */