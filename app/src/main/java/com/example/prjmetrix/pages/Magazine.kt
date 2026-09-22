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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prjmetrix.MagazineDto
import com.example.prjmetrix.MagazineRepository

@Composable
fun MagazineScreen(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current
    var magazine by remember { mutableStateOf<MagazineDto?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        MagazineRepository(context).weekly().fold({ magazine = it }, { error = it.message })
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF6EEE9))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextButton(onClick = onBackClick) { Text("‹ Back") }
        Text("✨ WEEKLY STYLE ZINE", style = MaterialTheme.typography.labelLarge)
        Text("Your week in review", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(18.dp))
        when {
            error != null -> Text(error!!)
            magazine == null -> CircularProgressIndicator()
            else -> {
                val m = magazine!!
                StatCard("Goals achieved", m.goalsAchieved.toString())
                StatCard("Journal entries", m.journalEntries.toString())
                StatCard("Outfits worn", m.outfitsWorn.toString())
                StatCard("Suggested outfits accepted", m.suggestionsAccepted.toString())
                StatCard("Suggested outfits skipped", m.suggestionsRejected.toString())
                Spacer(Modifier.height(12.dp))
                Text("👗 Outfit recap", style = MaterialTheme.typography.titleLarge)
                Text("Favourite outfit: ${m.favouriteOutfit ?: "No outfit data yet."}")
                Spacer(Modifier.height(12.dp))
                Text("🌸 Self-care reflection", style = MaterialTheme.typography.titleLarge)
                Text(
                    m.selfCareReflection
                        ?: "Complete the self-care quiz this week to add your reflection here."
                )
                Spacer(Modifier.height(12.dp))
                Text("📔 Journal rewind", style = MaterialTheme.typography.titleLarge)
                Text("Latest mood: ${m.latestMood ?: "No mood recorded."}")
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Row(
            Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, Modifier.weight(1f)); Text(
            value,
            style = MaterialTheme.typography.titleLarge
        )
        }
    }
}
