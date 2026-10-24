package com.example.prjmetrix

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color

// AVATAR CATALOG-describes every layer the avatar is drawn from.



// The three category buttons at the top right (hair, eyes, lips - as in the XML).
// The base body is NOT one of these: it is chosen with the left/right arrows instead.
// An enum is a fixed list of choices (Kotlin, n.d.).
enum class AvatarCategory(val label: String) {
    HAIR("Hair"),
    EYES("Eyes"),
    LIPS("Lips")
}

// The avatar is drawn on a canvas the size of the clothing layer in the XML (381 x 775 dp).
const val AVATAR_CANVAS_WIDTH = 381f
const val AVATAR_CANVAS_HEIGHT = 775f

// Where a layer sits on that canvas: x, y, width, height (in canvas units).
data class Placement(val x: Float, val y: Float, val w: Float, val h: Float)

val FullCanvas = Placement(0f, 0f, AVATAR_CANVAS_WIDTH, AVATAR_CANVAS_HEIGHT)  // layers that cover the whole avatar
private val HeadPlacement = Placement(47f, 96f, 283f, 242f)       // main hair      (imageView12)
private val ExtensionPlacement = Placement(50f, 99f, 283f, 236f)  // hair extension (imageView13)
private val GrinMouthPlacement = Placement(50f, 96f, 277f, 251f)  // grin mouth     (imageView14)
private val LipstickPlacement = Placement(47f, 101f, 285f, 241f)  // lipstick       (imageView15)
private val EyebrowPlacement = Placement(55f, 93f, 271f, 253f)    // eyebrows       (imageView11)

// One image drawn on the avatar: which drawable, where, and an optional tint colour.
data class AvatarLayer(
    val res: Int,
    val placement: Placement = FullCanvas,
    val tint: Color? = null,
    val blendMode: BlendMode = BlendMode.SrcIn
)

// One thing the user can pick from a list. It can be made of several layers.

data class AvatarPart(
    val ref: String,//  the name that is saved in the profile
    val thumbnail: Int, // the picture shown in the grid or base body
    val layers: List<AvatarLayer>
)


// TINT COLOURS

private val HairTint = Color(0xFF101010)   // #101010: colour of hair AND extension
private val LashTint = Color(0xD1310505)   // #D1310505: eyes_lashes1 overlay
const val IRIS_ALPHA = 0xB7 / 255f         // the XML iris tint #B7240C0C is ~72% opaque; the colour picker keeps that


// BASE BODY: chosen with the arrows next to the avatar

val bodyOptions = listOf(
    AvatarPart("base_body_f", R.drawable.base_body_f, listOf(AvatarLayer(R.drawable.base_body_f))),
    AvatarPart("base_body_e", R.drawable.base_body_e, listOf(AvatarLayer(R.drawable.base_body_e))),
    AvatarPart("base_body_g", R.drawable.base_body_g, listOf(AvatarLayer(R.drawable.base_body_g))),
    AvatarPart("base_body_b", R.drawable.base_body_b, listOf(AvatarLayer(R.drawable.base_body_b))),
    AvatarPart("base_body_h", R.drawable.base_body_h, listOf(AvatarLayer(R.drawable.base_body_h))),
    AvatarPart("base_body_l", R.drawable.base_body_l, listOf(AvatarLayer(R.drawable.base_body_l))),
    AvatarPart("base_body_v", R.drawable.base_body_v, listOf(AvatarLayer(R.drawable.base_body_v))),
    AvatarPart("base_body_b", R.drawable.base_body_b, listOf(AvatarLayer(R.drawable.base_body_b))),
    AvatarPart("base_body_c", R.drawable.base_body_c, listOf(AvatarLayer(R.drawable.base_body_c))),
    )


// HAIR PANEL: two lists side by side

// Small helpers so each option is one short line.
private fun hairPart(ref: String, res: Int) =
    AvatarPart(ref, res, listOf(AvatarLayer(res, HeadPlacement, HairTint)))

private fun extensionPart(ref: String, res: Int) =
    AvatarPart(ref, res, listOf(AvatarLayer(res, ExtensionPlacement, HairTint)))

// Left column: the hair itself
val hairOptions = listOf(
    hairPart("hair_1", R.drawable.hair_1),
    hairPart("hair_3", R.drawable.hair_3),
    hairPart("hair_5", R.drawable.hair_5),
    hairPart("hair_4", R.drawable.hair_4)
)

// Right column: the extensions (hair_bun is the bun)
val extensionOptions = listOf(
    extensionPart("extention_hair", R.drawable.extention_hair),
    extensionPart("hair_bun", R.drawable.hair_bun),
    extensionPart("pony_hair", R.drawable.pony_hair),
    extensionPart("strips_hair", R.drawable.strips_hair)
)

// EYES PANEL: the user only picks the iris colour

// name = label for you, hex = what is saved in the profile (#RRGGBB)
data class EyeColorOption(val name: String, val hex: String)

const val DEFAULT_EYE_COLOR = "#240C0C"    // the dark iris colour from the XML

val eyeColorOptions = listOf(
    EyeColorOption("Espresso", "#240C0C"),
    EyeColorOption("Brown", "#6D4C41"),
    EyeColorOption("Hazel", "#A67C2E"),
    EyeColorOption("Green", "#2E7D32"),
    EyeColorOption("Blue", "#1E63B5"),
    EyeColorOption("Grey", "#607D8B"),
    EyeColorOption("Violet", "#673AB7")
)

private val FallbackEyeColor = Color(0xFF240C0C)

// "#RRGGBB" text -> Compose Color. parseColor throws on bad text, so runCatching falls back
// to the default colour instead of crashing (Android Developers, 2026.).
fun parseEyeColor(hex: String): Color =
    runCatching { Color(android.graphics.Color.parseColor(hex)) }
        .getOrDefault(FallbackEyeColor)

// The three eye layers, bottom to top: white of the eye, lashes overlay, iris.
// Only the iris uses the chosen colour.
private fun eyeLayers(colorHex: String) = listOf(
    AvatarLayer(R.drawable.base_white_eyes),
    AvatarLayer(R.drawable.eyes_lashes1, tint = LashTint),
    AvatarLayer(R.drawable.eye1c, tint = parseEyeColor(colorHex), blendMode = BlendMode.Modulate)
)


// LIPS PANEL: mouth (the expression) on the left, lipstick on the right


val mouthOptions = listOf(
    AvatarPart(
        "grin_mouth", R.drawable.grin_mouth,
        listOf(AvatarLayer(R.drawable.grin_mouth, GrinMouthPlacement))
    ),
    // mouth_9 was
    AvatarPart(
        "mouth_9", R.drawable.mouth_9,
        listOf(AvatarLayer(R.drawable.mouth_9, FullCanvas))
    ),
            AvatarPart(
            "smile_mouth", R.drawable.smile_mouth,
    listOf(AvatarLayer(R.drawable.smile_mouth, FullCanvas))
)

)

// Saved in the profile when the user has no lipstick on.
const val NO_LIPSTICK = "none"
val lipstickOptions = listOf(
    AvatarPart(
        "lipstick6_grin", R.drawable.lipstick6_grin,
        listOf(AvatarLayer(R.drawable.lipstick6_grin, LipstickPlacement))
    )
)


// PARTS OF THE AVATAR THAT THE USER CANNOT CHANGE (yet)
private val clothingLayers = listOf(
    AvatarLayer(R.drawable.top2),
 
    AvatarLayer(R.drawable.grey_pants)

)

private val faceBaseLayers = listOf(
    AvatarLayer(R.drawable.lash_1),
    AvatarLayer(R.drawable.nose_2),
    AvatarLayer(R.drawable.neutro_eyebrows1, EyebrowPlacement)
)

// BUILD THE FINAL LIST OF LAYERS
// The order is the drawing order (first = at the back).
// body, clothes, lashes/nose/eyebrows, hair, extension, eyes, mouth, lipstick.
// An unknown reference will make the thing fallback to default

fun buildAvatarLayers(
    bodyRef: String,
    hairRef: String,
    extensionRef: String,
    eyeColor: String,
    mouthRef: String,
    lipstickRef: String
): List<AvatarLayer> {
    val body = bodyOptions.firstOrNull { it.ref == bodyRef } ?: bodyOptions.first()
    val hair = hairOptions.firstOrNull { it.ref == hairRef } ?: hairOptions.first()
    val extension = extensionOptions.firstOrNull { it.ref == extensionRef } ?: extensionOptions.first()
    val mouth = mouthOptions.firstOrNull { it.ref == mouthRef } ?: mouthOptions.first()
    // No fallback here: null means "no lipstick"
    val lipstick = lipstickOptions.firstOrNull { it.ref == lipstickRef }

    return body.layers + clothingLayers + faceBaseLayers +
            hair.layers + extension.layers +
            eyeLayers(eyeColor) +
            mouth.layers + (lipstick?.layers ?: emptyList())
}
/* References
Kotlin, 2026. Enum classes. [online] Available at: < https://kotlinlang.org/docs/enum-classes.html > [Accessed 21 September 2026]
Android Developers, 2026. Color. [online] Available at: < https://developer.android.com/reference/android/graphics/Color > [Accessed 21 September 2026]
*/