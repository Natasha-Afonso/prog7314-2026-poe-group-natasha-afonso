package com.example.prjmetrix


data class ProfileItemDTO(
    val displayName: String = "",
    val personality: String = "",
    val birthday: String? = null,          // "2003-09-15"
    val bodyKey: String = "base_body_f",
    val hairKey: String = "hair_1",
    val eyesKey: String = "eye1c",
    val lipsKey: String = "lipstick6_grin"
)

