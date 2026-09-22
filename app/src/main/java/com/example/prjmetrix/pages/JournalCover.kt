package com.example.prjmetrix.pages

import androidx.compose.animation.Crossfade
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


@Composable
fun JournalCover(onOpenJournal: () -> Unit) {
    Scaffold(containerColor = Color(0xFFFAF6EC)) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            BookCover(onClick = onOpenJournal) //calls the book design that opens to JournalList screen
            Spacer(modifier = Modifier.height(24.dp))
            JournalPrompts() //displays journal prompts under the book
        }
    }
}

//GeeksforGeeks, 2025)
private val journalPrompts = listOf(
    "Write about something you are grateful for",
    "What stood out to you today?",
    "What have you been overthinking about?",
    "Journaling reveals your behavioural patterns",
    "Lets reflect on how far you've come",
    "What is something you need to let go of",
    "What would your future self thank you for",
    "Have you ever made a pros and cons list about yourself?"
)

@Composable
private fun JournalPrompts() {
    var prompt by remember {mutableStateOf(0)}
    //(StackOverflow, 2023)
    LaunchedEffect(Unit) {
        while(true){
            delay(6_000) //changes prompt every 6 seconds
            prompt = (prompt + 1) % journalPrompts.size
        }
    }

    //(Kostadinov, 2025)
    Crossfade(targetState = prompt){i ->
        Text(
            text = journalPrompts[i],
            color = Color(0xFF2B2B2B),
            fontSize = 14.sp, textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun BookCover(onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "bookScale")

    Row(
        modifier = Modifier
            .scale(scale)
            .width(240.dp)
            .height(320.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        // spine
        Box(
            modifier = Modifier
                .width(14.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                .background(Color(0xFF5B2A2A))
        )

        // cover
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                .background(
                    Brush.linearGradient(listOf(Color(0xFFB9D7E8),Color(0xFFF3E4A8)))
                ),
            contentAlignment = Alignment.Center
        ) {
            StitchBorder()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "MeTrix",
                    color = Color(0xFF5B2A2A),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Journal",
                    color = Color(0xFF7A3E3E),
                    fontSize = 16.sp
                )
            }
        }
    }
}

//customized stitch for book cover
@Composable
private fun StitchBorder() {
    Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        drawRoundRect(
            color = Color(0xFF5B2A2A).copy(alpha = 0.35f),
            style = Stroke(
                width = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f),
            topLeft = Offset.Zero
        )
    }
}

/*References

Android Developers, 2026. Jetpack Compose Tutorial. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/develop/ui/compose/tutorial > [Accessed 17 September 2026]
Geeksforgeeks, 2025. Kotlin list: listof(). (Version 10.0) [Source Code]. Available at: < https://www.geeksforgeeks.org/kotlin/kotlin-list-listof/ > [Accessed 22 September 2026]
Kostadinov, D., 2025. Top 3 Most common animations you can use in your jetpack compose project. (Version 10.0) [Source Code]. Available at: < https://proandroiddev.com/top-3-most-common-animations-you-can-use-in-your-jetpack-compose-project-9bb92f5311a2 > [Accessed 22 September 2026]
StackOverflow, 2023. How to call a function every 5 seconds in Jetpack Compose. (Version 10.0) [Source Code]. Available at: <https://stackoverflow.com/questions/70081950/how-to-call-a-function-every-5-second-in-jetpack-compose > [Accessed 22 September 2026]
 */