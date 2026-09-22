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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MagazineScreen(
    onBackClick: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFC1DBE8)
    val darkBrown = Color(0xFF43302E)
    val journalBrown = Color(0xFF5A3A35)
    val paleYellow = Color(0xFFFFF1B5)
    val polaroidPink = Color(0xFFF2DCD6)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(20.dp)
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "‹",
                fontSize = 38.sp,
                color = darkBrown,
                modifier = Modifier
                    .clickable {
                        onBackClick()
                    }
                    .padding(end = 8.dp)
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "🪞",
                fontSize = 24.sp
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .background(paleYellow)
                .padding(
                    horizontal = 12.dp,
                    vertical = 7.dp
                )
        ) {

            Text(
                text = "✨ STYLE ZINE #08",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = darkBrown
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )



        Text(
            text = "Lookbook &\nFavorites 🪞",
            fontSize = 34.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold,
            color = darkBrown
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )



        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {



            Card(
                modifier = Modifier
                    .weight(0.9f)
                    .height(210.dp)
                    .graphicsLayer {
                        rotationZ = -3f
                    },
                shape = RoundedCornerShape(3.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(
                                RoundedCornerShape(2.dp)
                            )
                            .background(polaroidPink),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "★",
                            fontSize = 34.sp,
                            color = Color.White
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "★ Weekly Fit",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkBrown
                    )
                }
            }



            Card(
                modifier = Modifier
                    .weight(1.1f)
                    .graphicsLayer {
                        rotationZ = 2f
                    },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = paleYellow
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "⚡️ ACCESSORY SPOTLIGHT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkBrown
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "The Gold Rings & Tote",
                        fontSize = 18.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkBrown
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "• Essential daily stacking rings",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = darkBrown
                    )

                    Text(
                        text = "• Canvas everyday tote",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = darkBrown
                    )

                    Text(
                        text = "• Vibe: Effortless & Chic",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = darkBrown
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )



        Text(
            text = "✨ MOST LOVED ACCESSORIES",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = darkBrown
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )



        MagazineAccessoryCard(
            emoji = "💍",
            title = "Chunky Gold Rings",
            subtitle = "★ 5.0  •  Top Pick"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )



        MagazineAccessoryCard(
            emoji = "👜",
            title = "Minimalist Tote",
            subtitle = "★ Daily Go-To"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )



        MagazineAccessoryCard(
            emoji = "🎧",
            title = "Retro Headphones",
            subtitle = "★ Focus Mode"
        )

        Spacer(
            modifier = Modifier.height(26.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = journalBrown
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "🌸 STYLE VIBE & JOURNAL REWIND",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = "A productive week of balance & design",
                    fontSize = 18.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                MagazineJournalRow(
                    title = "Mood Tracker",
                    value = "✨ Calm & Creative"
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                MagazineJournalRow(
                    title = "Focus Vibe",
                    value = "🎧 Deep Work"
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                MagazineJournalRow(
                    title = "Favourite accessory",
                    value = "💍 Gold Rings"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )
    }
}



// Accessory

@Composable
private fun MagazineAccessoryCard(
    emoji: String,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Accessory preview
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(
                        RoundedCornerShape(14.dp)
                    )
                    .background(
                        Color(0xFFF2DCD6)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = emoji,
                    fontSize = 28.sp
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF43302E)
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = "›",
                fontSize = 27.sp,
                color = Color(0xFF43302E)
            )
        }
    }
}

// Journal

@Composable
<<<<<<< Updated upstream
private fun MagazineJournalRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.75f)
        )

        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
=======
private fun StatCard(label: String, value: String) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, Modifier.weight(1f)); Text(
            value,
            style = MaterialTheme.typography.titleLarge
>>>>>>> Stashed changes
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MagazineScreenPreview() {
    MagazineScreen()
}