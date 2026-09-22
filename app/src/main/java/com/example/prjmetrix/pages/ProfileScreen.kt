package com.example.prjmetrix.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prjmetrix.AvatarCategory
import com.example.prjmetrix.AvatarPart
import com.example.prjmetrix.EyeColorOption
import com.example.prjmetrix.ProfileState
import com.example.prjmetrix.ProfileUiState
import com.example.prjmetrix.ProfileViewModel
import com.example.prjmetrix.R
import com.example.prjmetrix.bodyOptions
import com.example.prjmetrix.buildAvatarLayers
import com.example.prjmetrix.extensionOptions
import com.example.prjmetrix.eyeColorOptions
import com.example.prjmetrix.hairOptions
import com.example.prjmetrix.lipstickOptions
import com.example.prjmetrix.mouthOptions
import com.example.prjmetrix.parseEyeColor
import java.time.format.DateTimeFormatter


// PROFILE SCREEN (entry point)
// Loading / error / ready is decided here
//  (Android,2026) sealed-state pattern
@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    // collectAsState turns the ViewModel's flow into Compose state: whenever the state
    // changes, this screen redraws itself (Android Developers, n.d.).
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        ProfileUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        ProfileUiState.Error -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Couldn't load your profile.")
            Button(onClick = viewModel::load) { Text("Try again") }
        }

        is ProfileUiState.Ready -> ProfileContent(
            profile = state.profile,
            onBodySelected = viewModel::onBodySelected,
            onHairSelected = viewModel::onHairSelected,
            onExtensionSelected = viewModel::onExtensionSelected,
            onEyeColorSelected = viewModel::onEyeColorSelected,
            onMouthSelected = viewModel::onMouthSelected,
            onLipstickSelected = viewModel::onLipstickSelected
        )
    }
}


//Receives profile data and hand handles user interaction
@Composable
private fun ProfileContent(
    profile: ProfileState,
    onBodySelected: (String) -> Unit,
    onHairSelected: (String) -> Unit,
    onExtensionSelected: (String) -> Unit,
    onEyeColorSelected: (String) -> Unit,
    onMouthSelected: (String) -> Unit,
    onLipstickSelected: (String) -> Unit
) {
    // Which category button (Hair / Eyes / Lips) is active. rememberSaveable keeps this
    // choice across a screen rotation.
    var category by rememberSaveable { mutableStateOf(AvatarCategory.HAIR) }

    // Rebuild the layer list only when one of the avatar choices actually changes.
    val avatarLayers = remember(
        profile.bodyRef, profile.hairRef, profile.extensionRef,
        profile.eyeColor, profile.mouthRef, profile.lipstickRef
    ) {
        buildAvatarLayers(
            bodyRef = profile.bodyRef,
            hairRef = profile.hairRef,
            extensionRef = profile.extensionRef,
            eyeColor = profile.eyeColor,
            mouthRef = profile.mouthRef,
            lipstickRef = profile.lipstickRef
        )
    }

    // The left/right arrows ALWAYS cycle the base body, no matter which category button
    // is selected up top, because they are their own control (imageButton / imageButton2
    // in the XML), separate from the Hair/Eyes/Lips buttons and grid.
    val bodyIndex = bodyOptions.indexOfFirst { it.ref == profile.bodyRef }.coerceAtLeast(0)
    val canCycleBody = bodyOptions.size > 1   // greyed out until there is more than one body

    Box(modifier = Modifier.fillMaxSize()) {

        // Room background (stretched over the whole screen, like the XML's android:background)
        Image(
            painter = painterResource(id = R.drawable.img_1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Everything else sits below the status bar
        Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {

            // The avatar: full width at the top, so the feet land on the podium in the background
            AvatarView(
                layers = avatarLayers,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )

            // Top left: the profile card
            ProfileInfoCard(
                profile = profile,
                modifier = Modifier.align(Alignment.TopStart).padding(start = 16.dp, top = 48.dp)
            )

            // Top right: Hair / Eyes / Lips buttons (body has no button - it uses the arrows)
            CategoryButtons(
                selected = category,
                onSelect = { category = it },
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 16.dp, top = 8.dp)
            )

            // Right: the panel for the chosen category
            val panelModifier = Modifier.align(Alignment.TopEnd).padding(end = 16.dp, top = 64.dp)
            when (category) {
                // hair (left column) + extension (right column): the user picks one of each
                AvatarCategory.HAIR -> TwoColumnPanel(
                    leftOptions = hairOptions,
                    leftSelectedKey = profile.hairRef,
                    onLeftSelected = onHairSelected,
                    rightOptions = extensionOptions,
                    rightSelectedKey = profile.extensionRef,
                    onRightSelected = onExtensionSelected,
                    modifier = panelModifier
                )

                // eyes: only the colour of the iris
                AvatarCategory.EYES -> EyeColorPanel(
                    selectedHex = profile.eyeColor,
                    onSelect = onEyeColorSelected,
                    modifier = panelModifier
                )

                // mouth (the expression) + lipstick drawn on top of it
                AvatarCategory.LIPS -> TwoColumnPanel(
                    leftOptions = mouthOptions,
                    leftSelectedKey = profile.mouthRef,
                    onLeftSelected = onMouthSelected,
                    rightOptions = lipstickOptions,
                    rightSelectedKey = profile.lipstickRef,
                    onRightSelected = onLipstickSelected,
                    modifier = panelModifier
                )
            }

            // Previous / next arrows for the BASE BODY, either side of the body
            // (423dp from the top, matching imageButton / imageButton2 in the XML)
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .offset(y = 423.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ArrowButton(
                    iconRes = R.drawable.arrow_left_45dp_8,
                    enabled = canCycleBody,
                    onClick = {
                        // "+ size" then "% size" wraps around: before the first item comes the last
                        val previous = (bodyIndex - 1 + bodyOptions.size) % bodyOptions.size
                        onBodySelected(bodyOptions[previous].ref)
                    }
                )
                ArrowButton(
                    iconRes = R.drawable.arrow_right_45dp_8,
                    enabled = canCycleBody,
                    onClick = {
                        val next = (bodyIndex + 1) % bodyOptions.size
                        onBodySelected(bodyOptions[next].ref)
                    }
                )
            }
        }
    }
}


// Profile card

@Composable
private fun ProfileInfoCard(profile: ProfileState, modifier: Modifier = Modifier) {
    // the two Roboto Slab fonts from res/font, matching the XML's android:fontFamily
    val slabFont = remember {
        FontFamily(
            Font(R.font.robotoslab_extrabold, FontWeight.ExtraBold),
            Font(R.font.robotoslab_medium, FontWeight.Medium)
        )
    }
    val birthday = profile.birthday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))


    Card(
        modifier = modifier.width(153.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF431F1F))   // outer card colour
    ) {
        Box(modifier = Modifier.padding(7.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth().heightIn(min = 135.dp),
                shape = RectangleShape,
                colors = CardDefaults.cardColors(containerColor = Color(0x69F0EAEA))   // inner card colour
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.account_box_40px),
                        contentDescription = "Profile",
                        modifier = Modifier.size(32.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = profile.userName.uppercase(),
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 4.dp),
                        fontFamily = slabFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        maxLines = 1
                    )
                    InfoLine("Personality: ${profile.personality.label}", slabFont)
                    InfoLine("Birthday: $birthday", slabFont)
                    InfoLine("Aspiration: ${profile.aspiration.label}", slabFont)
                }
            }
        }
    }
}

@Composable
private fun InfoLine(text: String, fontFamily: FontFamily) {
    Text(
        text = text,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        color = Color.Black
    )
}


// GLASS BOX: for items
// Selecting an option brightens the fill and gives it a solid white border.
@Composable
private fun GlassBox(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(12.dp)   // matches <corners android:radius="12dp" />
    Box(
        modifier = modifier
            .clip(shape)
            // unselected = exactly the drawable's #1AFFFFFF fill; selected = a brighter version
            .background(if (selected) Color(0x40FFFFFF) else Color(0x1AFFFFFF))
            .border(
                // unselected = exactly the drawable's 1dp #60FFFFFF stroke; selected = thicker + white
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color.White else Color(0x60FFFFFF),
                shape = shape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content
    )
}


// TOP RIGHT: Hair / Eyes / Lips category buttons

@Composable
private fun CategoryButtons(
    selected: AvatarCategory,
    onSelect: (AvatarCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AvatarCategory.entries.forEach { c ->
            GlassBox(
                selected = c == selected,
                onClick = { onSelect(c) },
                modifier = Modifier.size(40.dp)
            ) {
                when (c) {
                    AvatarCategory.HAIR -> Image(painterResource(R.drawable.self_care_24px), c.label)
                    AvatarCategory.EYES -> Image(painterResource(R.drawable.visibility_24px), c.label)
                    AvatarCategory.LIPS -> Image(painterResource(R.drawable.lips_24px), c.label)
                }
            }
        }
    }
}


// TWO-COLUMN PANEL: used for Hair+Extension and for Mouth+Lipstick.

@Composable
private fun TwoColumnPanel(
    leftOptions: List<AvatarPart>,
    leftSelectedKey: String,
    onLeftSelected: (String) -> Unit,
    rightOptions: List<AvatarPart>,
    rightSelectedKey: String,
    onRightSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OptionColumn(leftOptions, leftSelectedKey, onLeftSelected)
        OptionColumn(rightOptions, rightSelectedKey, onRightSelected)
    }
}

// One scrollable column of glass thumbnails (one column of the hair grid)
@Composable
private fun OptionColumn(
    options: List<AvatarPart>,
    selectedKey: String,
    onSelect: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier.width(61.dp).heightIn(max = 320.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options, key = { it.ref }) { part ->
            GlassBox(
                selected = part.ref == selectedKey,
                onClick = { onSelect(part.ref) },
                modifier = Modifier.size(width = 61.dp, height = 69.dp)
            ) {
                Image(
                    painter = painterResource(id = part.thumbnail),
                    contentDescription = part.ref,
                    modifier = Modifier.fillMaxSize().padding(6.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}


// EYES PANEL: a column of colour swatches, one per eyeColorOptions entry

@Composable
private fun EyeColorPanel(
    selectedHex: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.width(61.dp).heightIn(max = 320.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(eyeColorOptions, key = { it.hex }) { option: EyeColorOption ->
            val selected = option.hex.equals(selectedHex, ignoreCase = true)
            GlassBox(
                selected = selected,
                onClick = { onSelect(option.hex) },
                modifier = Modifier.size(61.dp)
            ) {
                // a plain circle swatch in the option's colour
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(parseEyeColor(option.hex))
                )
            }
        }
    }
}

//Previous/next arrows for choosing base body

@Composable
private fun ArrowButton(
    iconRes: Int,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 63.dp, height = 73.dp)
            .alpha(if (enabled) 1f else 0.4f)         // greyed out when there is nothing to go to
            .background(Color(0x60FFFFFF))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = iconRes), contentDescription = null)
    }
}

/* References
Android Developers, 2026. Lists and grids. [online] Available at: < https://developer.android.com/jetpack/compose/lists > [Accessed 21 September 2026]
*/