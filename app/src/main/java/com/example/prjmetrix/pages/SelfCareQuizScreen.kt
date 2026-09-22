package com.example.prjmetrix.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.prjmetrix.SelfCareRepository

@Composable
fun SelfCareQuizScreen(onDone: () -> Unit = {}) {
    val context = LocalContext.current
    val questions = listOf(
        "I made time to rest this week.",
        "I did something that made me feel good.",
        "I paid attention to my emotions.",
        "I spent time away from unnecessary stress.",
        "I know one thing I want to improve next week."
    )
    var index by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var reflection by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Self-care check-in", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))
        Text(questions[index], style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                score++; nextOrFinish(index, questions.size) {
                index = it; reflection = if (it == questions.lastIndex) reflection else reflection
            }
            }) { Text("Yes") }
            OutlinedButton(onClick = { nextOrFinish(index, questions.size) { index = it } }) {
                Text(
                    "Not yet"
                )
            }
        }
        if (index == questions.lastIndex) {
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                reflection,
                { reflection = it },
                Modifier.fillMaxWidth(),
                label = { Text("What did you reflect on?") })
            Spacer(Modifier.height(12.dp))
            Button(
                enabled = !saving && reflection.isNotBlank(),
                onClick = {
                    saving = true; scope.launch {
                    SelfCareRepository(context).save(
                        score,
                        reflection
                    ); saving = false; onDone()
                }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (saving) "Saving…" else "Finish quiz") }
        }
    }
}

private fun nextOrFinish(index: Int, size: Int, set: (Int) -> Unit) {
    if (index < size - 1) set(index + 1)
}

