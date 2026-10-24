package com.example.prjmetrix.pages





import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prjmetrix.QuizAnswer
import com.example.prjmetrix.StyleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Sky = Color(0xFFC1DBE8)
private val DarkBrown = Color(0xFF43302E)
private val Brown = Color(0xFF5A3A35)
private val OptionColors = listOf(
    Color(0xFFFFF1B5), Color(0xFFF2DCD6), Color(0xFFDDEFE4), Color(0xFFE3DDF5), Color(0xFFD6ECF7)
)

private data class QuizOption(val label: String, val emoji: String)

private data class QuizQuestion(
    val text: String,
    val hero: String,
    val options: List<QuizOption>
)

private val quizQuestions = listOf(
    QuizQuestion(
        "What's your vibe this week?", "🌈",
        listOf(
            QuizOption("Radiant", "☀️"), QuizOption("Calm", "🌿"), QuizOption("Tired", "😴"),
            QuizOption("Stressed", "😰"), QuizOption("Low", "🌧️")
        )
    ),
    QuizQuestion(
        "Which self-care move was your MVP?", "🏆",
        listOf(
            QuizOption("Drinking water", "💧"), QuizOption("Moving my body", "🏃"),
            QuizOption("Skincare & hygiene", "🧴"), QuizOption("Quiet time", "📖"),
            QuizOption("Time with people", "💬")
        )
    ),
    QuizQuestion(
        "If your week was an outfit, it would be…", "👗",
        listOf(
            QuizOption("A cozy hoodie", "🧸"), QuizOption("A sparkly party look", "✨"),
            QuizOption("Gym fit", "💪"), QuizOption("Pajamas all day", "🛌"),
            QuizOption("A power suit", "😎")
        )
    ),
    QuizQuestion(
        "How well did you rest?", "🌙",
        listOf(QuizOption("Really well", "🛌"), QuizOption("Okay", "🙂"), QuizOption("Not enough", "🥱"))
    ),
    QuizQuestion(
        "How kind were you to yourself?", "💖",
        listOf(
            QuizOption("Very kind", "💖"), QuizOption("Mostly kind", "🙂"),
            QuizOption("I was hard on myself", "😔")
        )
    ),
    QuizQuestion(
        "What do you want more of next week?", "🔮",
        listOf(
            QuizOption("Movement", "🏃"), QuizOption("Rest", "😴"), QuizOption("Creativity", "🎨"),
            QuizOption("Connection", "🤝"), QuizOption("Fun", "🎈")
        )
    )
)

private val feelingScores = mapOf("Radiant" to 5, "Calm" to 4, "Tired" to 2, "Stressed" to 2, "Low" to 1)

//A playful title for the result screen, based on how they said they feel
private fun resultTitle(feeling: String?): Pair<String, String> = when (feeling) {
    "Radiant" -> "Main Character Energy" to "☀️"
    "Calm" -> "Zen Master" to "🌿"
    "Tired" -> "Recharge Mode" to "🔋"
    "Stressed" -> "Deep Breath, Superstar" to "🫶"
    "Low" -> "Soft Reset Season" to "🌈"
    else -> "Check-in Champion" to "🌸"
}

private fun nextWeekTip(wish: String?): String = when (wish) {
    "Movement" -> "Tick off a movement goal on your habit list. Even a short walk counts!"
    "Rest" -> "Plan an early night, and lay out a cosy outfit for tomorrow."
    "Creativity" -> "Mix two pieces you've never worn together and plan it in your outfit calendar."
    "Connection" -> "Pick an outfit for a coffee date with someone you like."
    "Fun" -> "Plan a bold outfit just because. No occasion needed."
    else -> "Keep showing up for yourself. You're doing great."
}

@Composable
fun SelfCareQuizScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { StyleRepository(context) }

    var index by remember { mutableStateOf(0) }
    val answers = remember { mutableStateListOf<QuizAnswer>() }
    var finished by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }
    //the option that was just tapped, lights up before moving on
    var picked by remember(index) { mutableStateOf<String?>(null) }

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

    fun choose(question: QuizQuestion, option: QuizOption) {
        if (picked != null || saving) return
        picked = option.label
        scope.launch {
            delay(380)
            answers.add(QuizAnswer(question = question.text, answer = option.label, emoji = option.emoji))
            if (index < quizQuestions.lastIndex) index += 1 else save()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Sky)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "‹",
                fontSize = 38.sp,
                color = DarkBrown,
                modifier = Modifier.clickable { onBackClick() }.padding(end = 8.dp)
            )
            Text("Self-care check-in 🌸", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
        }

        Spacer(Modifier.height(16.dp))

        if (finished) {
            ResultCard(answers.toList(), onDone = onBackClick)
            return@Column
        }

        //a row of little flowers that bloom as you go through the quiz
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            quizQuestions.indices.forEach { i ->
                Text(if (i < index) "🌸" else if (i == index) "🌼" else "⚪", fontSize = 18.sp)
            }
            Spacer(Modifier.weight(1f))
            Text("${index + 1} / ${quizQuestions.size}", fontSize = 13.sp, color = Brown)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (index + (if (picked != null) 1f else 0f)) / quizQuestions.size },
            modifier = Modifier.fillMaxWidth(),
            color = Brown
        )
        Spacer(Modifier.height(18.dp))

        AnimatedContent(
            targetState = index,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
                }
            },
            label = "question"
        ) { i ->
            val question = quizQuestions[i]
            QuestionPage(
                question = question,
                picked = if (i == index) picked else null,
                onPick = { option -> choose(question, option) }
            )
        }

        Spacer(Modifier.height(6.dp))
        if (saving) Text("Saving your reflection…", fontSize = 13.sp, color = Brown)
        saveError?.let { msg ->
            Text(msg, fontSize = 13.sp, color = Color(0xFF9B2C2C))
            Button(onClick = { save() }) { Text("Retry") }
        }

        if (index > 0 && !saving && picked == null) {
            TextButton(onClick = {

                while (answers.size > index - 1) answers.removeAt(answers.lastIndex)
                saveError = null
                index -= 1
            }) { Text("‹ Back", color = Brown) }
        }
    }
}

@Composable
private fun QuestionPage(
    question: QuizQuestion,
    picked: String?,
    onPick: (QuizOption) -> Unit
) {
    //the big emoji pops in with a little bounce
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val heroScale by animateFloatAsState(
        targetValue = if (shown) 1f else 0.3f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "hero"
    )

    Column(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(question.hero, fontSize = 64.sp, modifier = Modifier.scale(heroScale))
        }
        Spacer(Modifier.height(6.dp))
        Text(
            question.text,
            fontSize = 23.sp, lineHeight = 29.sp, fontWeight = FontWeight.Bold, color = DarkBrown,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(18.dp))

        question.options.forEachIndexed { n, option ->
            OptionCard(
                option = option,
                color = OptionColors[n % OptionColors.size],
                isPicked = picked == option.label,
                dimmed = picked != null && picked != option.label,
                onClick = { onPick(option) }
            )
        }
    }
}

@Composable
private fun OptionCard(
    option: QuizOption,
    color: Color,
    isPicked: Boolean,
    dimmed: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isPicked) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pick"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .scale(scale)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (dimmed) color.copy(alpha = 0.45f) else color),
        border = if (isPicked) BorderStroke(3.dp, Brown) else null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(option.emoji, fontSize = 28.sp)
            Spacer(Modifier.width(14.dp))
            Text(
                option.label, fontSize = 17.sp, color = DarkBrown, fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            if (isPicked) Text("✓", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Brown)
        }
    }
}

@Composable
private fun ResultCard(answers: List<QuizAnswer>, onDone: () -> Unit) {
    val feeling = answers.firstOrNull()
    val wish = answers.lastOrNull()?.answer
    val (title, emoji) = resultTitle(feeling?.answer)

    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val pop by animateFloatAsState(
        targetValue = if (shown) 1f else 0.2f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "result"
    )

    Card(
        modifier = Modifier.fillMaxWidth().scale(pop),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎉  ✨  🎊", fontSize = 26.sp)
            Spacer(Modifier.height(6.dp))
            Text(emoji, fontSize = 72.sp)
            Spacer(Modifier.height(4.dp))
            Text("Your week in a word", fontSize = 13.sp, color = Color.Gray)
            Text(
                title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DarkBrown,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                answers.forEach { Text(it.emoji, fontSize = 26.sp) }
            }

            Spacer(Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1B5)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("For next week", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Brown)
                    Spacer(Modifier.height(4.dp))
                    Text(nextWeekTip(wish), fontSize = 14.sp, color = DarkBrown)
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(
                "Saved! It will show up in this week's magazine.",
                fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(14.dp))
            Button(onClick = onDone) { Text("Done") }
        }
    }
}