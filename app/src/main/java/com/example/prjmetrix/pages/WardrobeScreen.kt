package com.example.prjmetrix.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import  androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import  androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.prjmetrix.R

//Wardrobe Screen
@Composable
fun WardrobeScreen(
    onPlannerClick: () -> Unit ={},
    onTryOnClick: (Int) -> Unit ={})
{
    val background = Color(0xFFC1DBE8)
    val brown = Color(0xFF43302E)
    val cream = Color(0xFFFFF1B5)
    val border = Color(0x1A43302E)

    Column(
        modifier = Modifier.fillMaxSize()
            .background(background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top =32.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(
                text ="Wardrobe",
                color = brown,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick =onPlannerClick,
                colors = buttonColors(
                    containerColor = brown,
                    contentColor = cream,
                ),
                shape = RoundedCornerShape(10.dp)
            ){
                Text("📆 Planner", fontSize= 12.sp)
            }
        }
        Column(
            modifier = Modifier.weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top =16.dp, bottom =24.dp)
        )
        {
            Text(
                text = "For today (27°C ☀️)",
                color = brown,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 24.dp, top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WardrobeOutfitCard(
                    top = R.drawable.pink_top,
                    bottom = R.drawable.ripped_shorts,
                    shoes = R.drawable.sneakers1,
                    accessory = R.drawable.necklace,
                    description = "Casual vibe to feel the summer breeze",
                    onTryOn = { onTryOnClick(1) }
                )
                WardrobeOutfitCard(
                    top = R.drawable.white_corset,
                    bottom = R.drawable.denim_maxi,
                    shoes = R.drawable.heels,
                    accessory = R.drawable.blue_bag,
                    description = "Chic mixed with casual to give off baddie vibes",
                    onTryOn = { onTryOnClick(2) }
                )
            }
            Text(
                text = "Accessories",
                color = brown,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 24.dp, top = 28.dp)
            )
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 24.dp, top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            )
            {
                WardrobeAccessoryCard(R.drawable.pink_bag, "Leather Handbag")
                WardrobeAccessoryCard(R.drawable.blackishgold_glasses, "Glasses")
                WardrobeAccessoryCard(R.drawable.watch2, "White Smart watch")
            }

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 24.dp, top = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            )
            {
                WardrobeCategory(R.drawable.necklace, "All", selected = true)
                WardrobeCategory(R.drawable.blue_bag, "Bags")
                WardrobeCategory(R.drawable.necklace, "Belts")
                WardrobeCategory(R.drawable.necklace, "Necklaces")
                WardrobeCategory(R.drawable.white_corset, "Blazers")
                WardrobeCategory(R.drawable.sneakers1, "Footwear")
                WardrobeCategory(R.drawable.pink_top, "Jackets")
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WardrobeItemCard(R.drawable.ripped_shorts, "Denim Shorts", Modifier.weight(1f))
                WardrobeItemCard(R.drawable.sneakers1, "Sneakers", Modifier.weight(1f))
                WardrobeItemCard(R.drawable.blue_bag, "Casual Bag", Modifier.weight(1f))

            }
        }
    }
}

@Composable
private fun WardrobeOutfitCard(
    top: Int,
    bottom: Int,
    shoes: Int,
    accessory: Int,
    description: String,
    onTryOn: () -> Unit
) {
    val brown = Color(0xFF43302E)
    val cream = Color(0xFFFFF1B5)

    Column(modifier = Modifier.width(170.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(Color.White),
            border = BorderStroke(1.dp, Color(0x1A43302E)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                Image(
                    painter = painterResource(accessory),
                    contentDescription = null,
                    modifier = Modifier.size(45.dp).align(Alignment.TopStart),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(top),
                    contentDescription = null,
                    modifier = Modifier.size(85.dp).align(Alignment.TopCenter).offset(y = 16.dp),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(bottom),
                    contentDescription = null,
                    modifier = Modifier.width(120.dp).height(100.dp).align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(shoes),
                    contentDescription = null,
                    modifier = Modifier.width(70.dp).height(50.dp).align(Alignment.BottomCenter),
                    contentScale = ContentScale.Fit
                )
            }
        }
        Text(
            text = description,
            color = brown,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = onTryOn,
            modifier = Modifier.fillMaxWidth().height(38.dp).padding(top = 0.dp),
            colors = buttonColors(
                containerColor = brown,
                contentColor = cream
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Text("♧  Try On", fontSize = 12.sp)
        }
    }
}

@Composable
private fun WardrobeAccessoryCard(image: Int, name: String) {
    Column(modifier = Modifier.width(130.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().height(130.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1A43302E))
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = name,
                modifier = Modifier.fillMaxSize().padding(20.dp),
                contentScale = ContentScale.Fit
            )
        }
        Text(name, color = Color(0xFF43302E), fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
private fun WardrobeCategory(image: Int, name: String, selected: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp)) {
        Card(
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(Color.White),
            border = androidx.compose.foundation.BorderStroke(
                if (selected) 1.5.dp else 1.dp,
                if (selected) Color(0xFF43302E) else Color(0x1A43302E)
            )
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = name,
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentScale = ContentScale.Fit
            )
        }
        Text(name, color = Color(0xFF43302E), fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
private fun WardrobeItemCard(image: Int, name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth().height(130.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1A43302E))
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = name,
                modifier = Modifier.fillMaxSize().padding(20.dp),
                contentScale = ContentScale.Fit
            )
        }
        Text(name, color = Color(0xFF43302E), fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun WardrobeScreenPreview() {
    WardrobeScreen()
}
