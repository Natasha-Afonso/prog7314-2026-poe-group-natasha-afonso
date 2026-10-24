package com.example.prjmetrix.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prjmetrix.FurnitureCatalog
import com.example.prjmetrix.FurnitureRepository
import com.example.prjmetrix.PurchaseResult
import com.example.prjmetrix.R
import com.example.prjmetrix.WalletRepository
import java.text.NumberFormat
import kotlinx.coroutines.launch

// Colors matching the XML
val ChipYellow = Color(0xFFFFF1B5)
val CardStroke = Color(0x603343302E)

data class ShopItem(
    val imageRes: Int,
    val price: Int,
    val category: String,
    // Set for furniture, which is what the shop can actually sell right now.
    // Clothes and accessories have no key until the wardrobe is connected.
    val furnitureKey: String? = null,
    val name: String = ""
)

data class ShopCategory(
    val label: String,
    val selected: Boolean = false
)

@Composable
fun ShopScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedCategory by remember { mutableStateOf("All") }
    val snackbar = remember { SnackbarHostState() }

    // The balance and the inventory both come from Room, so the shop updates the moment a
    // purchase goes through (Android Developers, 2026).
    val wallet = remember { WalletRepository(context.applicationContext) }
    val furnitureRepo = remember { FurnitureRepository(context.applicationContext) }
    val coins by wallet.observeCoins().collectAsState(initial = 0)
    val owned by furnitureRepo.observeFurniture().collectAsState(initial = emptyList())

    val categories = listOf("All", "Hats", "Shoes", "Furniture", "Bows", "Scarfs")

    // Furniture comes from the catalog, so a new catalog entry shows up here automatically
    val furnitureItems = FurnitureCatalog.items.map {
        ShopItem(it.drawableRes, it.price, it.category, furnitureKey = it.key, name = it.name)
    }

    // Placeholder drawable ids - swap for your real R.drawable references
    val otherItems = listOf(
        ShopItem(R.drawable.redheels, 320, "Shoes", name = "Red heels"),
        ShopItem(R.drawable.bow, 126, "Bows", name = "Bow"),
        ShopItem(R.drawable.one_piece, 230, "Dresses", name = "One piece"),
        ShopItem(R.drawable.pink_bag, 580, "Bags", name = "Pink bag"),
        ShopItem(R.drawable.necklace, 150, "Jewellery", name = "Necklace"),
        ShopItem(R.drawable.sneakers1, 400, "Shoes", name = "Sneakers")
    )

    val items = (furnitureItems + otherItems).filter {
        selectedCategory == "All" || it.category == selectedCategory
    }

    Scaffold(
        containerColor = BgBlue,
        snackbarHost = { SnackbarHost(snackbar) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgBlue)
                    .padding(start = 12.dp, top = 14.dp, end = 16.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = BrandBrown
                    )
                }

                Text(
                    text = "Shop",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandBrown,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ChipYellow),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.coin),
                            contentDescription = "Coins",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = NumberFormat.getInstance().format(coins),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandBrown,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }

            // Category chips row
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                categories.forEachIndexed { index, category ->
                    CategoryChip(
                        label = category,
                        selected = category == selectedCategory,
                        onClick = { selectedCategory = category }
                    )
                    if (index != categories.lastIndex) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            // Shopping grid - two per row
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BgBlue)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { item ->
                            ShopItemCard(
                                item = item,
                                ownedCount = owned.count { it.itemKey == item.furnitureKey },
                                canAfford = coins >= item.price,
                                onBuy = {
                                    val key = item.furnitureKey ?: return@ShopItemCard
                                    scope.launch {
                                        val message = when (wallet.buyFurniture(key)) {
                                            PurchaseResult.SUCCESS ->
                                                "${item.name} bought! Find it in Edit mode."
                                            PurchaseResult.NOT_ENOUGH_COINS ->
                                                "Not enough coins for ${item.name}."
                                            PurchaseResult.UNKNOWN_ITEM ->
                                                "That item isn't available."
                                        }
                                        snackbar.currentSnackbarData?.dismiss()
                                        snackbar.showSnackbar(message)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // If odd number of items in the last row, keep spacing even
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) BrandBrown else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = if (selected) null
        else BorderStroke(1.dp, CardStroke)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (selected) ChipYellow else BrandBrown,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
fun ShopItemCard(
    item: ShopItem,
    ownedCount: Int,
    canAfford: Boolean,
    onBuy: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Only furniture can be bought for now; the rest waits for the wardrobe
    val buyable = item.furnitureKey != null
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xC9FFFFFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0x60FFFFFF))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (item.name.isNotEmpty()) {
                Text(
                    text = if (ownedCount > 0) "${item.name} · owned $ownedCount" else item.name,
                    fontSize = 12.sp,
                    color = BrandBrown,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = "Coins",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = NumberFormat.getInstance().format(item.price),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandBrown,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
                Button(
                    onClick = onBuy,
                    enabled = buyable && canAfford,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChipYellow,
                        disabledContainerColor = ChipYellow.copy(alpha = 0.45f)
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 12.dp,
                        vertical = 2.dp
                    )
                ) {
                    Text(
                        text = if (!buyable) "Soon" else "Buy",
                        color = BrandBrown,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}