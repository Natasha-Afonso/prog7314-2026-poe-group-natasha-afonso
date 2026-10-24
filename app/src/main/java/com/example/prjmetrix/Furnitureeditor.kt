package com.example.prjmetrix

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

// Colours are private to HomeScreen.kt, so this file keeps its own set (soft lavender/pink panel)
private val EditInk = Color(0xFF7A4B92)
private val EditInkSoft = Color(0xFFA37DB8)
private val EditAccent = Color(0xFFD46BC8)
private val EditBorder = Color(0xFFE79BE0)
private val PanelBg = Color(0xFFF0D3F7)
private val PanelEdge = Color(0xFFE2B6EE)
private val TileBg = Color(0xFFEBCDF4)
private val TileBorder = Color(0xFFDDB0EE)

// FURNITURE LAYER
// Draws every placed piece of furniture at its saved position. Positions are stored as
// fractions (0..1) of the free space in the room, so they land in the same spot on any screen.
// In edit mode a piece can be dragged; tap it to select it and show the "x" that puts it back
// in the tray.
@Composable
fun FurnitureLayer(
    furniture: List<FurnitureEntity>,
    editMode: Boolean,
    selectedId: Int?,
    onSelect: (Int?) -> Unit,
    onMove: (id: Int, x: Float, y: Float) -> Unit,
    onPickUp: (id: Int) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val roomW = maxWidth
        val roomH = maxHeight

        // Tapping empty floor in edit mode clears the selection
        if (editMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelect(null) }
                    )
            )
        }

        furniture.filter { it.placed }.forEach { item ->
            val def = FurnitureCatalog.find(item.itemKey) ?: return@forEach
            val freeW = (roomW - def.widthDp.dp).coerceAtLeast(0.dp)
            val freeH = (roomH - def.heightDp.dp).coerceAtLeast(0.dp)

            // Live fractions while dragging; saved to Room only when the finger lifts
            var fx by remember(item.localId, item.posX) { mutableFloatStateOf(item.posX) }
            var fy by remember(item.localId, item.posY) { mutableFloatStateOf(item.posY) }
            val selected = editMode && selectedId == item.localId

            Box(
                modifier = Modifier
                    .offset(x = freeW * fx, y = freeH * fy)
                    .size(def.widthDp.dp, def.heightDp.dp)
            ) {
                Image(
                    painter = painterResource(id = def.drawableRes),
                    contentDescription = def.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (selected) {
                                Modifier.border(BorderStroke(2.dp, EditAccent), RoundedCornerShape(12.dp))
                            } else Modifier
                        )
                        .then(
                            if (editMode) {
                                Modifier
                                    .clickable(
                                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onSelect(item.localId) }
                                    )
                                    .pointerInput(item.localId, roomW, roomH) {
                                        detectDragGestures(
                                            onDragStart = { onSelect(item.localId) },
                                            onDragEnd = { onMove(item.localId, fx, fy) },
                                            onDragCancel = { onMove(item.localId, fx, fy) }
                                        ) { change, drag ->
                                            change.consume()
                                            val freeWpx = with(density) { freeW.toPx() }
                                            val freeHpx = with(density) { freeH.toPx() }
                                            if (freeWpx > 0f) fx = (fx + drag.x / freeWpx).coerceIn(0f, 1f)
                                            if (freeHpx > 0f) fy = (fy + drag.y / freeHpx).coerceIn(0f, 1f)
                                        }
                                    }
                            } else Modifier
                        ),
                    contentScale = ContentScale.Fit
                )

                // Puts the piece back in the tray
                if (selected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 14.dp, y = (-14).dp)
                            .size(44.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, EditBorder, CircleShape)
                            .clickable {
                                onSelect(null)
                                onPickUp(item.localId)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Put away", tint = EditInk, modifier = Modifier.size(26.dp))
                    }
                }
            }
        }
    }
}

// EDIT MODE TOP BAR
// Replaces the normal top bar while editing: a label chip and a Done button.
@Composable
fun EditModeTopBar(onDone: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .shadow(6.dp, RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.92f))
                .border(1.5.dp, EditBorder, RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, tint = EditAccent, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Editing room", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditInk)
        }

        Box(
            modifier = Modifier
                .shadow(6.dp, RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .background(Brush.verticalGradient(listOf(Color(0xFFE88ADB), EditAccent)))
                .clickable(onClick = onDone)
                .padding(horizontal = 20.dp, vertical = 9.dp)
        ) {
            Text("Done", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
    }
}

// FURNITURE TRAY
// The bottom panel listing everything the user owns, laid out as a grid of tiles under a
// category chip. Tap a tile to put the piece in the room (or back in the tray if it is already
// placed). The panel sits at the very bottom because the app's bottom navigation is hidden
// while editing.
@Composable
fun FurnitureTray(
    furniture: List<FurnitureEntity>,
    onToggle: (FurnitureEntity) -> Unit,
    modifier: Modifier = Modifier,
    heightFraction: Float = 0.42f
) {
    val panelShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(heightFraction)
            .shadow(16.dp, panelShape)
            .clip(panelShape)
            .background(PanelBg)
            .border(2.dp, PanelEdge, panelShape)
            .padding(start = 14.dp, end = 14.dp, top = 14.dp)
    ) {
        // Category chip + owned count
        Row(verticalAlignment = Alignment.CenterVertically) {
            val chipShape = RoundedCornerShape(50)
            Row(
                modifier = Modifier
                    .clip(chipShape)
                    .background(Brush.verticalGradient(listOf(Color(0xFFFBD6F7), Color(0xFFF3B8EE))))
                    .border(2.dp, EditBorder, chipShape)
                    .padding(start = 18.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Furniture", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = EditInk)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = EditInk, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.weight(1f))
            Text("${furniture.size} owned", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditInkSoft)
        }

        Spacer(Modifier.height(12.dp))

        // Grid sits in a lighter rounded well, like the item list in the reference
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color(0xFFF6E3FA))
        ) {
            if (furniture.isEmpty()) {
                Text(
                    text = "Nothing here yet. Furniture you buy in the shop will show up here.",
                    fontSize = 13.sp,
                    color = EditInkSoft,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 28.dp)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 10.dp, end = 10.dp, top = 10.dp,
                        bottom = 10.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(furniture, key = { it.localId }) { item ->
                        FurnitureTile(item = item, onClick = { onToggle(item) })
                    }
                }
            }
        }
    }
}

@Composable
private fun FurnitureTile(item: FurnitureEntity, onClick: () -> Unit) {
    val def = FurnitureCatalog.find(item.itemKey) ?: return
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(TileBg)
            .border(2.dp, if (item.placed) EditAccent else TileBorder, shape)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = def.drawableRes),
            contentDescription = def.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        // Small corner tag on pieces that are already standing in the room
        if (item.placed) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-8).dp, y = (-8).dp)
                    .size(22.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, bottomEnd = 10.dp))
                    .background(EditAccent),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = "In room", tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
    }
}