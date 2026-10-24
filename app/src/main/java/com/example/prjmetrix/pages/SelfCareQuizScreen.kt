package com.example.prjmetrix.pages



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prjmetrix.QuizAnswer
import com.example.prjmetrix.StyleRepository
import kotlinx.coroutines.launch


// Short weekly check in
//The answers feed the reflection page of the weekly magazine.
val selfCareQuizQuestions: List<Pair<String, List<Pair<String, String>>>> = listOf(
    "How have you been feeling this week?" to listOf(
        "Radiant" to "☀️", "Calm" to "🌿", "Tired" to "😴", "Stressed" to "😰", "Low" to "🌧️"
    ),
    "Which self-care activity helped you most?" to listOf(
        "Drinking water" to "💧", "Moving my body" to "🏃", "Skincare & hygiene" to "🧴",
        "Quiet time" to "📖", "Time with people" to "💬"
    ),
    "How well did you rest?" to listOf(
        "Really well" to "🛌", "Okay" to "🙂", "Not enough" to "🥱"
    ),
    "How kind were you to yourself?" to listOf(
        "Very kind" to "💖", "Mostly kind" to "🙂", "I was hard on myself" to "😔"
    ),
    "What do you want more of next week?" to listOf(
        "Movement" to "🏃", "Rest" to "😴", "Creativity" to "🎨", "Connection" to "🤝", "Fun" to "🎈"
    )
)

@Composable
fun SelfCareQuizScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { StyleRepository(context) }

    val backgroundColor = Color(0xFFC1DBE8)
    val darkBrown = Color(0xFF43302E)
    val brown = Color(0xFF5A3A35)

    var index by remember { mutableStateOf(0) }
    val answers = remember { mutableStateListOf<QuizAnswer>() }
    var finished by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }


    val feelingScores = mapOf("Radiant" to 5, "Calm" to 4, "Tired" to 2, "Stressed" to 2, "Low" to 1)

    fun save() {
        saving = true
        saveError = null
        scope.launch {
            val score = feelingScores[answers.firstOrNull()?.answer] ?: 3
            repository.saveQuiz(score, answers.toList())
                .onSuccess { finished = true }
                .onFailure { saveError = it.message }
            saving = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "‹",
                fontSize = 38.sp,
                color = darkBrown,
                modifier = Modifier
                    .clickable { onBackClick() }
                    .padding(end = 8.dp)
            )
            Text("Self-care check-in 🌸", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = darkBrown)
        }

        Spacer(Modifier.height(20.dp))

        if (finished) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✨", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Reflection saved", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = darkBrown)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "It will appear in this week's magazine.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onBackClick) { Text("Done") }
                }
            }
        } else {
            val (question, options) = selfCareQuizQuestions[index]

            Text(
                "Question ${index + 1} of ${selfCareQuizQuestions.size}",
                fontSize = 13.sp,
                color = brown
            )
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (index + 1f) / selfCareQuizQuestions.size },
                modifier = Modifier.fillMaxWidth(),
                color = brown
            )
            Spacer(Modifier.height(24.dp))
            Text(question, fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold, color = darkBrown)
            Spacer(Modifier.height(18.dp))

            options.forEach { (label, emoji) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clickable {
                            answers.add(QuizAnswer(question = question, answer = label, emoji = emoji))
                            if (index < selfCareQuizQuestions.lastIndex) {
                                index += 1
                            } else if (!saving) {
                                save()
                            }
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(emoji, fontSize = 24.sp)
                        Spacer(Modifier.width(14.dp))
                        Text(label, fontSize = 16.sp, color = darkBrown, fontWeight = FontWeight.Medium)
                    }
                }
            }

            if (saving) Text("Saving…", fontSize = 13.sp, color = brown)
            saveError?.let { msg ->
                Text(msg, fontSize = 13.sp, color = Color(0xFF9B2C2C))
                Button(onClick = { save() }) { Text("Retry") }
            }

            if (index > 0 && !saving) {
                TextButton(onClick = {

                    if (answers.size > index) answers.removeLastOrNull()
                    answers.removeLastOrNull()
                    saveError = null
                    index -= 1
                }) { Text("Back", color = brown) }
            }
        }
    }
}
