package com.example.prjmetrix

// The JSON the profile API sends and receives. Property names match the API's camelCase JSON,
// so Gson maps them automatically. Everything here is plain text because JSON has no
//  the app turns these values into layers and colours.
data class ProfileItemDTO(
    val userName: String = "",
    val personality: String = "",          // enum name, e.g. "CURIOUS"
    val birthday: String? = null,          // "2003-09-15" (null if not set)
    val aspiration: String = "",           // enum name, e.g. "ORGANISED"

    val bodyRef: String = "base_body_f",
    val hairRef: String = "hair_1",
    val extensionRef: String = "extention_hair",
    val eyeColor: String = "#240C0C",
    val mouthRef: String = "grin_mouth",
    val lipstickRef: String = NO_LIPSTICK
)

