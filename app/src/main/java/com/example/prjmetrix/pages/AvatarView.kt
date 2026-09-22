package com.example.prjmetrix.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.prjmetrix.AVATAR_CANVAS_HEIGHT
import com.example.prjmetrix.AVATAR_CANVAS_WIDTH
import com.example.prjmetrix.AvatarLayer

// Draws the avatar: every layer stacked on a 381 x 775 canvas, scaled to whatever width it is given.
@Composable
fun AvatarView(layers: List<AvatarLayer>, modifier: Modifier = Modifier) {
    // aspectRatio keeps the canvas shape (width : height = 381 : 775)
    // BoxWithConstraints tells us how much room we were given (Android Developers, 2026)
    BoxWithConstraints(modifier = modifier.aspectRatio(AVATAR_CANVAS_WIDTH / AVATAR_CANVAS_HEIGHT)) {

        // how many real dp one canvas unit is worth (1.0 when the width is exactly 381dp)
        val scale = maxWidth.value / AVATAR_CANVAS_WIDTH

        // Later layers are layered on top of earlier ones
        layers.forEach { layer ->
            val p = layer.placement
            Image(
                painter = painterResource(id = layer.res),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                // tint uses BlendMode.SrcIn: the colour shows wherever the image has pixels
                colorFilter = layer.tint?.let { ColorFilter.tint(it) },
                modifier = Modifier
                    .offset(x = (p.x * scale).dp, y = (p.y * scale).dp)          // move to its spot on the canvas
                    .size(width = (p.w * scale).dp, height = (p.h * scale).dp)   // scale its size the same way
            )
        }
    }
}

/* References
Android Developers, 2026. BoxWithConstraints. [online] Available at: < https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/BoxWithConstraints.composable > [Accessed 21 September 2026]
Android Developers, 2026. Customize an image. [online] Available at: < https://developer.android.com/develop/ui/compose/graphics/images/customize > [Accessed 21 September 2026]
*/