package com.example.prjmetrix.pages



import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.prjmetrix.ClothingItem
import com.example.prjmetrix.ClothingViewFact
import com.example.prjmetrix.ClothingViewModel
import com.example.prjmetrix.LocationHelper
import com.example.prjmetrix.Outfit
import com.example.prjmetrix.OutfitPiece
import com.example.prjmetrix.StyleViewModel
import com.example.prjmetrix.isWeatherPick
import com.example.prjmetrix.StyleViewModelFactory
import com.example.prjmetrix.SuggestionDecision
import com.example.prjmetrix.SuggestionRecord
import com.example.prjmetrix.WeatherSnapshot
import java.io.File

private enum class WardrobeTab(val label: String) { CLOSET("Closet"), OUTFITS("Outfits"), WISHLIST("Wishlist") }

@Composable
fun WardrobeScreen(
    onPlannerClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val clothingViewModel: ClothingViewModel = viewModel(factory = ClothingViewFact(context))
    val styleViewModel: StyleViewModel = viewModel(factory = StyleViewModelFactory(context))

    val uiState by clothingViewModel.uiState.collectAsState()
    val style by styleViewModel.uiState.collectAsState()

    //outfits made by "Wear it" on the daily suggestion are kept out of the user's own list
    val myOutfits = style.outfits.filterNot { it.isWeatherPick }

    var tab by remember { mutableStateOf(WardrobeTab.CLOSET) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showWishlistDialog by remember { mutableStateOf(false) }
    var showOutfitBuilder by remember { mutableStateOf(false) }
    var showWallpaperMenu by remember { mutableStateOf(false) }
    var locationAnswered by remember { mutableStateOf(LocationHelper.hasPermission(context)) }
    var locationTick by remember { mutableStateOf(0) }

    //Asks for location once, so the weather and so the suggestion is for where the user is
    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        locationAnswered = true
        locationTick++
    }

    val wallpaperPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> if (uri != null) styleViewModel.setWallpaper(uri) }

    LaunchedEffect(Unit) {
        clothingViewModel.loadClothing()
        styleViewModel.loadOutfits()
        if (!LocationHelper.hasPermission(context)) {
            locationLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    //New suggestion whenever the wardrobe changes
    LaunchedEffect(uiState.clothing, locationTick) {
        styleViewModel.refreshSuggestion(uiState.clothing)
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        //Custom wallpaper
        if (style.wallpaperPath != null) {
            AsyncImage(
                model = File(style.wallpaperPath!!),
                contentDescription = "Wardrobe wallpaper",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.25f)))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("My Wardrobe", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        IconButton(onClick = { showWallpaperMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Wardrobe options")
                        }
                        DropdownMenu(
                            expanded = showWallpaperMenu,
                            onDismissRequest = { showWallpaperMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Change wallpaper") },
                                onClick = {
                                    showWallpaperMenu = false
                                    wallpaperPicker.launch("image/*")
                                }
                            )
                            if (style.wallpaperPath != null) {
                                DropdownMenuItem(
                                    text = { Text("Reset wallpaper") },
                                    onClick = {
                                        showWallpaperMenu = false
                                        styleViewModel.clearWallpaper()
                                    }
                                )
                            }
                        }
                    }
                    Button(onClick = onPlannerClick) { Text("Planner") }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Always visible, the user can wear it, skip it, or ask for another
            TodaySuggestionCard(
                weather = style.weather,
                weatherError = style.weatherError,
                reason = style.suggestionReason,
                suggestion = style.suggestion,
                wardrobeEmpty = uiState.clothing.isEmpty(),
                locationDenied = locationAnswered && !LocationHelper.hasPermission(context),
                onWear = { styleViewModel.wearSuggestion() },
                onSkip = { styleViewModel.skipSuggestion() },
                onReset = { styleViewModel.resetSuggestion() },
                onTryAnother = { styleViewModel.tryAnother(uiState.clothing) }
            )

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WardrobeTab.entries.forEach { t ->
                    FilterChip(
                        selected = tab == t,
                        onClick = { tab = t },
                        label = { Text(t.label) }
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Messages
            uiState.infoMessage?.let { msg ->
                MessageBanner(msg) { clothingViewModel.clearInfo() }
            }
            style.message?.let { msg ->
                MessageBanner(msg) { styleViewModel.clearMessage() }
            }

            when {
                uiState.isLoading && uiState.clothing.isEmpty() -> {
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null -> {
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(uiState.errorMessage ?: "Something went wrong.")
                            Spacer(Modifier.height(10.dp))
                            Button(onClick = {
                                clothingViewModel.clearError()
                                clothingViewModel.loadClothing()
                            }) { Text("Try Again") }
                        }
                    }
                }

                tab == WardrobeTab.CLOSET -> {
                    if (uiState.clothing.isEmpty()) {
                        EmptyState("Your wardrobe is empty.", "Add your first clothing item.", "Add Clothing") {
                            showAddDialog = true
                        }
                    } else {
                        ClothingGrid(uiState.clothing) { clothingViewModel.deleteClothing(it.id) }
                    }
                }

                tab == WardrobeTab.OUTFITS -> {
                    if (myOutfits.isEmpty()) {
                        EmptyState("No outfits yet.", "Combine pieces into an outfit and plan it on a day.", "Create outfit") {
                            showOutfitBuilder = true
                        }
                    } else {
                        OutfitList(myOutfits) { styleViewModel.deleteOutfit(it.id) }
                    }
                }

                else -> {
                    if (uiState.wishlist.isEmpty()) {
                        EmptyState(
                            "Your wishlist is empty.",
                            "Screenshot something you love from an online store and add it here.",
                            "Add screenshot"
                        ) { showWishlistDialog = true }
                    } else {
                        ClothingGrid(uiState.wishlist) { clothingViewModel.deleteClothing(it.id) }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                when (tab) {
                    WardrobeTab.CLOSET -> showAddDialog = true
                    WardrobeTab.OUTFITS -> showOutfitBuilder = true
                    WardrobeTab.WISHLIST -> showWishlistDialog = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 130.dp)   // sits above the bottom navigation bar
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }

        if (uiState.isSaving) {
            Box(
                Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Card {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(28.dp))
                        Spacer(Modifier.width(14.dp))
                        Text("Removing background & saving…")
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddClothingDialog(
            title = "Add Clothing",
            photoLabel = "Choose Photo",
            hint = "The background is removed automatically.",
            onDismiss = { showAddDialog = false },
            onAdd = { name, category, imageUri ->
                clothingViewModel.addClothing(name, category, imageUri)
                showAddDialog = false
            }
        )
    }

    if (showWishlistDialog) {
        AddClothingDialog(
            title = "Add to Wishlist",
            photoLabel = "Choose Screenshot",
            hint = "Upload a screenshot of an item you'd like to buy.",
            onDismiss = { showWishlistDialog = false },
            onAdd = { name, category, imageUri ->
                clothingViewModel.addWishlist(name, category, imageUri)
                showWishlistDialog = false
                tab = WardrobeTab.WISHLIST
            }
        )
    }

    if (showOutfitBuilder) {
        OutfitBuilderDialog(
            clothing = uiState.clothing,
            onDismiss = { showOutfitBuilder = false },
            onSave = { name, pieces, date ->
                styleViewModel.createOutfit(name, pieces, date)
                showOutfitBuilder = false
                tab = WardrobeTab.OUTFITS
            }
        )
    }
}

@Composable
private fun MessageBanner(message: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1B5))
    ) {
        Row(Modifier.padding(start = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(message, modifier = Modifier.weight(1f), fontSize = 13.sp, color = Color(0xFF43302E))
            TextButton(onClick = onDismiss) { Text("OK") }
        }
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String, buttonLabel: String, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(260.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(subtitle, fontSize = 13.sp)
            Spacer(Modifier.height(14.dp))
            Button(onClick = onClick) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(" $buttonLabel")
            }
        }
    }
}

@Composable
private fun ClothingGrid(items: List<ClothingItem>, onDelete: (ClothingItem) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 200.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = items, key = { it.id }) { item ->
            ClothingCard(item = item, onDelete = { onDelete(item) })
        }
    }
}

@Composable
private fun OutfitList(outfits: List<Outfit>, onDelete: (Outfit) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 200.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(outfits, key = { it.id }) { outfit ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(outfit.name, fontWeight = FontWeight.Bold)
                            Text("${outfit.pieces.size} pieces", fontSize = 12.sp, color = Color.Gray)
                        }
                        TextButton(onClick = { onDelete(outfit) }) { Text("Delete") }
                    }
                    Spacer(Modifier.height(6.dp))
                    PieceRow(outfit.pieces)
                }
            }
        }
    }
}

@Composable
fun PieceRow(pieces: List<OutfitPiece>, size: Int = 64) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(pieces) { piece ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(size.dp)) {
                ClothingThumb(piece.imageUrl, piece.name, Modifier.size(size.dp))
                Text(piece.name, fontSize = 10.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun TodaySuggestionCard(
    weather: WeatherSnapshot?,
    weatherError: String?,
    reason: String,
    suggestion: SuggestionRecord?,
    wardrobeEmpty: Boolean,
    locationDenied: Boolean,
    onWear: () -> Unit,
    onSkip: () -> Unit,
    onReset: () -> Unit,
    onTryAnother: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f))
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("Today's suggestion", fontWeight = FontWeight.Bold, color = Color(0xFF43302E))

            if (weather != null) {
                Text(weather.summary + "  (${weather.minC}°–${weather.maxC}°)", fontSize = 13.sp, color = Color(0xFF43302E))
                if (weather.usedDefaultLocation) {
                    Text(
                        if (locationDenied) "Location is off, so this is a default location's weather."
                        else "Showing a default location's weather.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            } else if (weatherError != null) {
                Text(weatherError, fontSize = 13.sp, color = Color.Gray)
            } else {
                Text("Checking the weather…", fontSize = 13.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(8.dp))

            when {
                wardrobeEmpty ->
                    Text("Add some clothes and we'll suggest what to wear each day.", fontSize = 13.sp)

                suggestion == null -> Unit

                suggestion.pieces.isEmpty() ->
                    Text(
                        "We need a few more items (tops, bottoms or dresses, and shoes) to build an outfit.",
                        fontSize = 13.sp
                    )

                else -> {
                    if (reason.isNotBlank()) Text(reason, fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(6.dp))
                    PieceRow(suggestion.pieces)
                    Spacer(Modifier.height(8.dp))

                    when (suggestion.decision) {
                        SuggestionDecision.WEARING -> {
                            Text("✅ You're wearing this today", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            TextButton(onClick = onReset) { Text("Change my mind") }
                        }

                        SuggestionDecision.SKIPPED -> {
                            Text("You decided not to wear this today.", fontSize = 13.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = onWear) { Text("Wear it after all") }
                                OutlinedButton(onClick = onTryAnother) { Text("Try another") }
                            }
                        }

                        else -> {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = onWear) { Text("Wear it") }
                                OutlinedButton(onClick = onSkip) { Text("Not today") }
                                TextButton(onClick = onTryAnother) { Text("Another") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClothingCard(
    item: ClothingItem,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp)) {
            ClothingThumb(item.imageUrl, item.name, Modifier.fillMaxWidth().height(170.dp))
            Spacer(Modifier.height(8.dp))
            Text(item.name, fontWeight = FontWeight.Medium)
            Text(item.category, fontSize = 12.sp, color = Color.Gray)
            TextButton(onClick = onDelete) { Text("Delete") }
        }
    }
}

@Composable
fun AddClothingDialog(
    title: String = "Add Clothing",
    photoLabel: String = "Choose Photo",
    hint: String = "",
    onDismiss: () -> Unit,
    onAdd: (String, String, Uri?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Tops") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val categories = listOf("Tops", "Bottoms", "Dresses", "Shoes", "Accessories", "Jackets")

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                if (hint.isNotBlank()) {
                    Text(hint, fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Clothing name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                //Category dropdown
                Box(Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { categoryExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Category: $category") }

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    category = option
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (imageUri == null) photoLabel else "Change image") }

                if (imageUri != null) {
                    Spacer(Modifier.height(10.dp))
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onAdd(name.trim(), category, imageUri) }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
