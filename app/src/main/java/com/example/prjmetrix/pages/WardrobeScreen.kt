package com.example.prjmetrix.pages

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.prjmetrix.ClothingItem
import com.example.prjmetrix.ClothingViewFact
import com.example.prjmetrix.ClothingViewModel
import com.example.prjmetrix.OutfitRepository
import com.example.prjmetrix.WardrobePreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(
    onPlannerClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val vm: ClothingViewModel = viewModel(
        factory = ClothingViewFact(context)
    )

    val state by vm.uiState.collectAsState()

    var selectedTab by remember {
        mutableStateOf(0)
    }

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var wallpaperUri by remember {
        mutableStateOf<Uri?>(null)
    }

    // DataStore preferences
    val wardrobePreferences = remember {
        WardrobePreferences(context)
    }

    // Coroutine scope used when saving wallpaper
    val scope = rememberCoroutineScope()

    // Wallpaper image picker
    val wallpaperPicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {
                wallpaperUri = uri

                scope.launch {
                    wardrobePreferences.setWallpaper(
                        uri.toString()
                    )
                }
            }
        }

    // Load wardrobe and saved wallpaper
    LaunchedEffect(Unit) {

        vm.load()

        wardrobePreferences.wallpaper.collect { savedWallpaper ->

            wallpaperUri =
                savedWallpaper?.let {
                    Uri.parse(it)
                }
        }
    }

    // Load outfits so planner data can be refreshed
    LaunchedEffect(Unit) {

        runCatching {
            OutfitRepository(context).getOutfits()
        }
    }

    val items: List<ClothingItem> =
        if (selectedTab == 0) {
            state.clothing
        } else {
            state.wishlist
        }

    Scaffold(
        floatingActionButton = {

            FloatingActionButton(
                onClick = {
                    showAddDialog = true
                }
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add clothing"
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (wallpaperUri != null) {

                AsyncImage(
                    model = wallpaperUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.15f
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "My Wardrobe",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    IconButton(
                        onClick = {
                            wallpaperPicker.launch("image/*")
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Change wallpaper"
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(
                    onClick = onPlannerClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Planner")
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                TabRow(
                    selectedTabIndex = selectedTab
                ) {

                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                        },
                        text = {
                            Text("My Clothes")
                        }
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                        },
                        text = {
                            Text("Wishlist")
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                if (state.isLoading) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Text("Loading...")
                    }


                } else if (state.errorMessage != null) {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = state.errorMessage
                                ?: "Something went wrong."
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Button(
                            onClick = {
                                vm.clearError()
                                vm.load()
                            }
                        ) {

                            Text("Retry")
                        }
                    }


                } else if (items.isEmpty()) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text =
                                if (selectedTab == 0) {
                                    "No clothes added yet."
                                } else {
                                    "Your wishlist is empty."
                                }
                        )
                    }

                } else {

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        items(
                            items = items,
                            key = { clothing ->
                                clothing.id
                            }
                        ) { clothing ->

                            ClothingCard(
                                item = clothing,
                                onDelete = {
                                    vm.delete(clothing.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {

        AddWardrobeDialog(
            wishlist = selectedTab == 1,

            onDismiss = {
                showAddDialog = false
            },

            onAddClothing = {
                    name,
                    category,
                    uri,
                    removeBackground ->

                vm.addClothing(
                    name = name,
                    category = category,
                    uri = uri,
                    removeBackground = removeBackground
                )

                showAddDialog = false
            },

            onAddWishlist = {
                    name,
                    uri ->

                vm.addWishlist(
                    name = name,
                    uri = uri
                )

                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ClothingCard(
    item: ClothingItem,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column {

            if (!item.imageUrl.isNullOrBlank()) {

                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )

            } else {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text("No image")
                }
            }

            Column(
                modifier = Modifier.padding(10.dp)
            ) {

                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = item.category,
                    style = MaterialTheme.typography.bodyMedium
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.align(Alignment.End)
                ) {

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription =
                            "Delete ${item.name}"
                    )
                }
            }
        }
    }
}

@Composable
private fun AddWardrobeDialog(
    wishlist: Boolean,

    onDismiss: () -> Unit,

    onAddClothing: (
        String,
        String,
        Uri?,
        Boolean
    ) -> Unit,

    onAddWishlist: (
        String,
        Uri?
    ) -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var category by remember {
        mutableStateOf("Top")
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var removeBackground by remember {
        mutableStateOf(true)
    }

    val imagePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            imageUri = uri
        }

    val categories = listOf(
        "Top",
        "Bottom",
        "Dress",
        "Shoes",
        "Outerwear",
        "Accessory"
    )

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {

            Text(
                if (wishlist) {
                    "Add Wishlist Item"
                } else {
                    "Add Clothing"
                }
            )
        },

        text = {

            Column {

                OutlinedTextField(
                    value = name,

                    onValueChange = {
                        name = it
                    },

                    label = {
                        Text("Name")
                    },

                    singleLine = true,

                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                if (!wishlist) {

                    Text("Category")

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {

                        categories
                            .take(3)
                            .forEach { value ->

                                TextButton(
                                    onClick = {
                                        category = value
                                    }
                                ) {

                                    Text(value)
                                }
                            }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {

                        categories
                            .drop(3)
                            .forEach { value ->

                                TextButton(
                                    onClick = {
                                        category = value
                                    }
                                ) {

                                    Text(value)
                                }
                            }
                    }


                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = removeBackground,

                            onCheckedChange = {
                                removeBackground = it
                            }
                        )

                        Text("Remove background")
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Button(
                    onClick = {
                        imagePicker.launch("image/*")
                    },

                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        if (imageUri == null) {
                            "Choose Image"
                        } else {
                            "Image Selected"
                        }
                    )
                }
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    if (name.isBlank()) {
                        return@Button
                    }

                    if (wishlist) {

                        onAddWishlist(
                            name.trim(),
                            imageUri
                        )

                    } else {

                        onAddClothing(
                            name.trim(),
                            category,
                            imageUri,
                            removeBackground
                        )
                    }
                }
            ) {

                Text("Save")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Cancel")
            }
        }
    )
}