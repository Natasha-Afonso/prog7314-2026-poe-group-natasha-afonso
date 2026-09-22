package com.example.prjmetrix.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.prjmetrix.ClothingItem
import com.example.prjmetrix.ClothingViewFact
import com.example.prjmetrix.ClothingViewModel

@Composable
fun WardrobeScreen(
    onPlannerClick: () -> Unit = {}
) {

    val context = LocalContext.current

    val clothingViewModel: ClothingViewModel =
        viewModel(
            factory = ClothingViewFact(context)
        )

    val uiState by clothingViewModel
        .uiState
        .collectAsState()

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        clothingViewModel.loadClothing()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // Top section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "My Wardrobe"
                )

                Button(
                    onClick = onPlannerClick
                ) {
                    Text("Planner")
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // Show loading
            if (uiState.isLoading) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }

            }

            // Show error
            else if (uiState.errorMessage != null) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = uiState.errorMessage
                                ?: "Something went wrong."
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Button(
                            onClick = {
                                clothingViewModel.clearError()
                                clothingViewModel.loadClothing()
                            }
                        ) {

                            Text("Try Again")
                        }
                    }
                }
            }


            else if (uiState.clothing.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Your wardrobe is empty."
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Add your first clothing item."
                        )

                        Spacer(
                            modifier = Modifier.height(15.dp)
                        )

                        Button(
                            onClick = {
                                showAddDialog = true
                            }
                        ) {

                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add"
                            )

                            Text(" Add Clothing")
                        }
                    }
                }
            }

            // Show clothing
            else {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = uiState.clothing,
                        key = { item -> item.id }
                    ) { item ->

                        ClothingCard(
                            item = item,
                            onDelete = {
                                clothingViewModel.deleteClothing(
                                    item.id
                                )
                            }
                        )
                    }
                }
            }
        }

        // Add button
        FloatingActionButton(
            onClick = {
                showAddDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add clothing"
            )
        }
    }

    // Add clothing popup
    if (showAddDialog) {

        AddClothingDialog(

            onDismiss = {
                showAddDialog = false
            },

<<<<<<< Updated upstream
            onAdd = { name, category, imageUri ->
=======
            onAddClothing = { name,
                              category,
                              uri,
                              removeBackground ->
>>>>>>> Stashed changes

                clothingViewModel.addClothing(
                    name = name,
                    category = category,
<<<<<<< Updated upstream
                    imageUri = imageUri
=======
                    uri = uri,
                    removeBackground = removeBackground
                )

                showAddDialog = false
            },

            onAddWishlist = { name,
                              uri ->

                vm.addWishlist(
                    name = name,
                    uri = uri
>>>>>>> Stashed changes
                )

                showAddDialog = false
            }
        )
    }
}


@Composable
fun ClothingCard(
    item: ClothingItem,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(10.dp)
        ) {

            // Display image if there is one
            if (!item.imageUrl.isNullOrEmpty()) {

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
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text("No image")
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = item.name
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = item.category
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            TextButton(
                onClick = onDelete
            ) {

                Text("Delete")
            }
        }
    }
}


@Composable
fun AddClothingDialog(
    onDismiss: () -> Unit,
    onAdd: (
        String,
        String,
        Uri?
    ) -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var category by remember {
        mutableStateOf("Tops")
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    val categories = listOf(
        "Tops",
        "Bottoms",
        "Dresses",
        "Shoes",
        "Accessories",
        "Jackets"
    )

    // Opens the phone gallery
    val imagePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            imageUri = uri
        }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Add Clothing")
        },

        text = {

            Column {

                // Clothing name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text("Clothing name")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // Category
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Category")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = {
                            categoryExpanded = false
                        }
                    ) {

                        categories.forEach { item ->

                            DropdownMenuItem(

                                text = {
                                    Text(item)
                                },

                                onClick = {

                                    category = item
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Open dropdown when field is clicked
                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Button(
                    onClick = {
                        categoryExpanded = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("Choose Category")
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // Choose image
                Button(
                    onClick = {

                        imagePicker.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        if (imageUri == null) {
                            "Choose Photo"
                        } else {
                            "Change Photo"
                        }
                    )
                }

                // Image preview
                if (imageUri != null) {

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected clothing",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    if (name.isNotBlank()) {

                        onAdd(
                            name,
                            category,
                            imageUri
                        )
                    }
                }
            ) {

                Text("Add")
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