package com.example.prjmetrix.pages

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun JournalCover(onOpenJournal: () -> Unit) {
    Scaffold(containerColor = Color(0xFFFAF6EC)) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            BookCover(onClick = onOpenJournal)
        }
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