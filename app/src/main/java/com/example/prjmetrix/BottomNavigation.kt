package com.example.prjmetrix

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val font = FontFamily(
        Font(R.font.robotoslab_black, FontWeight.Black)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(Color(0x4CFFFFFF))
            .border(
                width = 1.dp,
                color = Color(0x60FFFFFF),
                shape = RoundedCornerShape(40.dp)
            )
            .padding(horizontal = 10.dp)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        NavigationItem(
            label = "Home",
            icon = R.drawable.home_24px,
            selected = currentRoute == "home",
            font = font,
            onClick = { onNavigate("home") }
        )

        NavigationItem(
            label = "Wardrobe",
            icon = R.drawable.checkroom_24px,
            selected = currentRoute == "wardrobe",
            font = font,
            onClick = { onNavigate("wardrobe") }
        )

        NavigationItem(
            label = "Journal",
            icon = R.drawable.add_notes_24px,
            selected = currentRoute == "journalcover",
            font = font,
            onClick = { onNavigate("journalcover") }
        )

        NavigationItem(
            label = "Custom",
            icon = R.drawable.dresser_24px,
            selected = currentRoute == "profile",
            font = font,
            onClick = { onNavigate("profile") }
        )
    }
}

@Composable
private fun NavigationItem(
    label: String,
    icon: Int,
    selected: Boolean,
    font: FontFamily,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier.size(25.dp)
        )

        Text(
            text = label,
            fontFamily = font,
            fontSize = if (label == "Home") 11.sp else 13.sp,
            color = Color(0xFF43302E)
        )
    }
}