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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prjmetrix.MagazineIssue
import com.example.prjmetrix.MagazineViewModel
import com.example.prjmetrix.MagazineViewModelFactory
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val Cream = Color(0xFFFFF1B5)
private val DarkBrown = Color(0xFF43302E)
private val JournalBrown = Color(0xFF5A3A35)
private val Pink = Color(0xFFF2DCD6)

//goals achieved, outfit choices and the self care reflection for one week.
@Composable
fun MagazineScreen(
    onBackClick: () -> Unit = {},
    onTakeQuiz: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: MagazineViewModel = viewModel(factory = MagazineViewModelFactory(context))
    val state by viewModel.uiState.collectAsState()
    val issue = state.issue

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC1DBE8))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "‹", fontSize = 38.sp, color = DarkBrown,
                modifier = Modifier.clickable { onBackClick() }.padding(end = 8.dp)
            )
            Spacer(Modifier.weight(1f))
            Text("🪞", fontSize = 24.sp)
        }

        Spacer(Modifier.height(10.dp))

        if (issue == null) {
            Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        val dateFmt = DateTimeFormatter.ofPattern("d MMM")


        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.clip(RoundedCornerShape(8.dp)).background(Cream).padding(horizontal = 12.dp, vertical = 7.dp)) {
                Text("✨ STYLE ZINE #${issue.issueNumber}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
            }
            Spacer(Modifier.weight(1f))
            Text(
                "‹", fontSize = 30.sp, color = DarkBrown,
                modifier = Modifier.clickable { viewModel.previousWeek() }.padding(horizontal = 10.dp)
            )
            Text(
                "›", fontSize = 30.sp,
                color = if (issue.isCurrentWeek) DarkBrown.copy(alpha = 0.25f) else DarkBrown,
                modifier = Modifier.clickable(enabled = !issue.isCurrentWeek) { viewModel.nextWeek() }
                    .padding(horizontal = 10.dp)
            )
        }

        Spacer(Modifier.height(12.dp))
        Text("Your Week\nin Style 🪞", fontSize = 34.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
        Spacer(Modifier.height(4.dp))
        Text(
            "${issue.weekStart.format(dateFmt)} – ${issue.weekEnd.format(dateFmt)}" +
                    if (issue.isCurrentWeek) "  ·  this week so far" else "",
            fontSize = 13.sp, color = JournalBrown
        )

        if (state.isLoading) {
            Spacer(Modifier.height(8.dp))
            Text("Updating…", fontSize = 12.sp, color = JournalBrown)
        }

        Spacer(Modifier.height(24.dp))

        GoalsSection(issue)
        Spacer(Modifier.height(24.dp))
        OutfitsSection(issue)
        Spacer(Modifier.height(24.dp))
        ReflectionSection(issue, onTakeQuiz)
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun GoalsSection(issue: MagazineIssue) {
    SectionTitle("🏆 GOALS ACHIEVED")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Cream)
    ) {
        Column(Modifier.padding(18.dp)) {
            if (!issue.goalsLoaded) {
                Text("Couldn't load all your goals (check your connection).", fontSize = 12.sp, color = JournalBrown)
                Spacer(Modifier.height(8.dp))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Stat("${issue.goalsAchieved}", "goals\nachieved")
                Stat("${issue.perfectDays}", "perfect\ndays")
                Stat("🪙 ${issue.coinsEarned}", "coins\nearned")
            }
            if (issue.topGoals.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                issue.topGoals.take(4).forEach { goal ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                        Text("✓ ${goal.label}", Modifier.weight(1f), fontSize = 14.sp, color = DarkBrown)
                        Text(
                            if (goal.daysCompleted == 1) "1 day" else "${goal.daysCompleted} days",
                            fontSize = 13.sp, fontWeight = FontWeight.Medium, color = JournalBrown
                        )
                    }
                }
            } else if (issue.goalsLoaded) {
                Spacer(Modifier.height(10.dp))
                Text("No goals completed this week. A fresh start is always one habit away.", fontSize = 13.sp, color = DarkBrown)
            }
        }
    }
}

@Composable
private fun Stat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
        Text(label, fontSize = 11.sp, lineHeight = 14.sp, color = JournalBrown)
    }
}

@Composable
private fun OutfitsSection(issue: MagazineIssue) {
    SectionTitle("👗 LOOKBOOK")

    if (!issue.outfitsLoaded) {
        Text("Couldn't load all your outfit data (check your connection).", fontSize = 12.sp, color = JournalBrown)
        Spacer(Modifier.height(8.dp))
    }

    if (issue.worn.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Text(
                "No outfits logged this week. Tap \"Wear it\" on a daily suggestion or plan an outfit in your calendar.",
                modifier = Modifier.padding(16.dp), fontSize = 13.sp, color = DarkBrown
            )
        }
        return
    }

    issue.worn.forEach { worn ->
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        worn.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        fontWeight = FontWeight.Bold, color = JournalBrown, fontSize = 13.sp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(worn.title, fontWeight = FontWeight.Bold, color = DarkBrown, modifier = Modifier.weight(1f))
                    if (worn.fromSuggestion) Text("☁️ weather pick", fontSize = 11.sp, color = Color.Gray)
                }
                Spacer(Modifier.height(8.dp))
                PieceRow(worn.pieces, size = 56)
            }
        }
    }

    val decided = issue.suggestionsWorn + issue.suggestionsSkipped
    if (decided > 0) {
        Text(
            "You wore ${issue.suggestionsWorn} of $decided daily suggestion${if (decided == 1) "" else "s"}" +
                    if (issue.suggestionsSkipped > 0) " and chose your own look on ${issue.suggestionsSkipped}." else ".",
            fontSize = 13.sp, color = DarkBrown
        )
        Spacer(Modifier.height(10.dp))
    }

    if (issue.mostWornPieces.isNotEmpty()) {
        Text("MOST WORN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JournalBrown)
        Spacer(Modifier.height(8.dp))
        issue.mostWornPieces.forEach { (piece, count) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    ClothingThumb(piece.imageUrl, piece.name, Modifier.size(48.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(piece.name, fontWeight = FontWeight.Bold, color = DarkBrown, fontSize = 14.sp)
                        Text(piece.category, fontSize = 12.sp, color = Color.Gray)
                    }
                    Text("×$count", fontWeight = FontWeight.Bold, color = JournalBrown)
                }
            }
        }
    }
}

@Composable
private fun ReflectionSection(issue: MagazineIssue, onTakeQuiz: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = JournalBrown)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("🌸 SELF-CARE REFLECTION", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(14.dp))

            val reflection = issue.reflection
            if (reflection == null) {
                Text(
                    "You haven't done a self-care check-in for this week.",
                    fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f)
                )
                if (issue.isCurrentWeek) {
                    Spacer(Modifier.height(14.dp))
                    Button(onClick = onTakeQuiz) { Text("Take the check-in") }
                }
            } else {

                val feeling = reflection.answers.firstOrNull()
                val wish = reflection.answers.lastOrNull()
                if (feeling != null) {
                    Text(
                        "A ${feeling.answer.lowercase()} week ${feeling.emoji}",
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                }
                if (wish != null && reflection.answers.size > 1) {
                    Text(
                        "Next week you want more: ${wish.answer.lowercase()}",
                        fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(Modifier.height(14.dp))
                }
                reflection.answers.forEach { a ->
                    Column(Modifier.padding(vertical = 5.dp)) {
                        Text(a.question, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                        Text("${a.emoji} ${a.answer}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    }
                }
                if (issue.quizCount > 1) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Showing your latest of ${issue.quizCount} check-ins this week.",
                        fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
