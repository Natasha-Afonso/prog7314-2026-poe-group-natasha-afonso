package com.example.prjmetrix.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prjmetrix.R

@Composable
fun PlannerScreen(
    onBackClick: () -> Unit = {},
    onTodayClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Month") }
    var selectedDay by remember { mutableStateOf(19) }

    val backgroundColor = Color(0xFFC1DBE8)
    val cardColor = Color.White
    val darkText = Color(0xFF43302E)
    val accentColor = Color(0xFF5A3A35)


    val days = listOf(
        "", "", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "10", "11", "12",
        "13", "14", "15", "16", "17", "18", "19",
        "20", "21", "22", "23", "24", "25", "26",
        "27", "28", "29", "30"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
<<<<<<< Updated upstream
                    text = "‹",
                    fontSize = 38.sp,
                    color = darkText,
                    modifier = Modifier
                        .clickable {
                            onBackClick()
                        }
                        .padding(end = 8.dp)
=======
                    text =
                        "${
                            month.month.name.lowercase().replaceFirstChar { it.uppercase() }
                        } ${month.year}",
                    style =
                        MaterialTheme.typography.titleLarge
>>>>>>> Stashed changes
                )

                Text(
                    text = "Outfit Calendar",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkText
                )
            }

            Button(
                onClick = {
                    selectedDay = 19
                    selectedTab = "Month"
                    onTodayClick()
                },
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Today",
                    fontSize = 14.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                FilterChip(
                    selected = selectedTab == "Day",
                    onClick = {
                        selectedTab = "Day"
                    },
                    label = {
                        Text(
                            text = "Day"
                        )
                    }
                )

                FilterChip(
                    selected = selectedTab == "Month",
                    onClick = {
                        selectedTab = "Month"
                    },
                    label = {
                        Text(
                            text = "Month"
                        )
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )


        if (selectedTab == "Month") {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "‹",
                            fontSize = 28.sp,
                            color = darkText
                        )

                        Text(
                            text = "September 2026",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkText
                        )

                        Text(
                            text = "›",
                            fontSize = 28.sp,
                            color = darkText
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )


                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        listOf(
                            "S",
                            "M",
                            "T",
                            "W",
                            "T",
                            "F",
                            "S"
                        ).forEach { day ->

                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = day,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )



                    days.chunked(7).forEach { week ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {

                            week.forEach { day ->

                                val dayNumber =
                                    day.toIntOrNull()

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (
                                                dayNumber != null &&
                                                dayNumber == selectedDay
                                            ) {
                                                accentColor
                                            } else {
                                                Color.Transparent
                                            }
                                        )
                                        .clickable {
                                            if (dayNumber != null) {
                                                selectedDay = dayNumber
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {

                                    if (day.isNotEmpty()) {

                                        Text(
                                            text = day,
                                            fontSize = 13.sp,
                                            color =
                                                if (
                                                    dayNumber == selectedDay
                                                ) {
                                                    Color.White
                                                } else {
                                                    darkText
                                                },
                                            fontWeight =
                                                if (
                                                    dayNumber == selectedDay
                                                ) {
                                                    FontWeight.Bold
                                                } else {
                                                    FontWeight.Normal
                                                }
                                        )
                                    }
                                }
                            }

                            // Complete the final row
                            repeat(7 - week.size) {

                                Spacer(
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        } else {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "September $selectedDay, 2026",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Your planned outfit",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )



                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(
                                RoundedCornerShape(18.dp)
                            )
                            .background(
                                Color(0xFFF2DCD6)
                            ),
                        contentAlignment = Alignment.Center
                    ) {


                        Image(
                            painter = painterResource(
                                id = R.drawable.pink_top
                            ),
                            contentDescription = "Pink top",
                            modifier = Modifier
                                .width(105.dp)
                                .height(90.dp)
                                .offset(
                                    y = (-30).dp
                                ),
                            contentScale = ContentScale.Fit
                        )


                        Image(
                            painter = painterResource(
                                id = R.drawable.ripped_shorts
                            ),
                            contentDescription = "Denim shorts",
                            modifier = Modifier
                                .width(95.dp)
                                .height(70.dp)
                                .offset(
                                    y = 30.dp
                                ),
                            contentScale = ContentScale.Fit
                        )


                        Image(
                            painter = painterResource(
                                id = R.drawable.sneakers1
                            ),
                            contentDescription = "Sneakers",
                            modifier = Modifier
                                .width(75.dp)
                                .height(50.dp)
                                .offset(
                                    x = 45.dp,
                                    y = 65.dp
                                ),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "Casual Chic",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "Pink Top • Denim Shorts • Sneakers",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "☀️ 27°C",
                        fontSize = 13.sp,
                        color = darkText
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )


        Text(
            text = "Selected Date",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = darkText
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {


                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(
                            RoundedCornerShape(14.dp)
                        )
                        .background(
                            Color(0xFFFFE8E2)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = selectedDay.toString(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Planned Outfit",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = "Casual Chic",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "›",
                    fontSize = 30.sp,
                    color = darkText
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // Outfit details

        Text(
            text = "Outfit Details",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = darkText
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {


                Box(
                    modifier = Modifier
                        .width(145.dp)
                        .fillMaxHeight()
                        .clip(
                            RoundedCornerShape(18.dp)
                        )
                        .background(
                            Color(0xFFF2DCD6)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    // Pink top
                    Image(
                        painter = painterResource(
                            id = R.drawable.pink_top
                        ),
                        contentDescription = "Pink top",
                        modifier = Modifier
                            .width(85.dp)
                            .height(85.dp)
                            .offset(
                                y = (-30).dp
                            ),
                        contentScale = ContentScale.Fit
                    )

                    // Denim shorts
                    Image(
                        painter = painterResource(
                            id = R.drawable.ripped_shorts
                        ),
                        contentDescription = "Denim shorts",
                        modifier = Modifier
                            .width(80.dp)
                            .height(65.dp)
                            .offset(
                                y = 25.dp
                            ),
                        contentScale = ContentScale.Fit
                    )

                    // Sneakers
                    Image(
                        painter = painterResource(
                            id = R.drawable.sneakers1
                        ),
                        contentDescription = "Sneakers",
                        modifier = Modifier
                            .width(65.dp)
                            .height(45.dp)
                            .offset(
                                x = 35.dp,
                                y = 55.dp
                            ),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )



                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Casual Chic",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )

                    Spacer(
                        modifier = Modifier.height(7.dp)
                    )

                    Text(
                        text = "Pink Top",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "Denim Shorts",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "Sneakers",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "☀️ 27°C",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = darkText
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "View Outfit →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5A3A35),
                        modifier = Modifier.clickable {
                            // Add navigation here if required
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlannerScreenPreview() {
    PlannerScreen()
}