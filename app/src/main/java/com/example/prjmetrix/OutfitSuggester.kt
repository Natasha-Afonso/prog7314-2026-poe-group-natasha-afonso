package com.example.prjmetrix



import java.time.LocalDate
import kotlin.math.abs

//Picks an outfit from the user's own wardrobe to suit current day's weather
//Warmth is guessed from the item's category
object OutfitSuggester {

    private val warmWords = listOf(
        "coat", "hoodie", "sweater", "jumper", "knit", "wool", "fleece", "long sleeve", "jeans",
        "trouser", "pants", "legging", "boot", "scarf", "beanie", "puffer", "cardigan",
        "turtleneck", "thermal", "gloves"
    )
    private val coolWords = listOf(
        "short", "tank", "crop", "sandal", "sleeveless", "linen", "skirt", "tee", "t-shirt",
        "tshirt", "sundress", "cap", "sunglass", "flip", "vest", "strappy", "bikini"
    )
    private val rainWords = listOf("rain", "waterproof", "trench", "boot", "umbrella", "poncho")

    private fun String.hasAny(words: List<String>) = words.any { this.contains(it) }


    private fun warmth(item: ClothingItem): Int {
        val n = item.name.lowercase()
        var w = when (item.category) {
            "Jackets" -> 3
            else -> 2
        }
        if (n.hasAny(warmWords)) w += 1
        if (n.hasAny(coolWords)) w -= 1
        if (n.contains("coat") || n.contains("puffer")) w += 1
        return w.coerceIn(0, 4)
    }

    private fun targetWarmth(tempC: Int): Int = when {
        tempC >= 28 -> 0
        tempC >= 24 -> 1
        tempC >= 18 -> 2
        tempC >= 12 -> 3
        else -> 4
    }


    private fun pick(items: List<ClothingItem>, target: Int, rainy: Boolean, seed: Int): ClothingItem? {
        if (items.isEmpty()) return null
        fun score(i: ClothingItem): Int {
            var s = abs(warmth(i) - target) * 2
            if (rainy && i.name.lowercase().hasAny(rainWords)) s -= 3
            return s
        }
        val best = items.minOf { score(it) }
        val group = items.filter { score(it) == best }.sortedBy { it.id }
        return group[Math.floorMod(seed, group.size)]
    }

    fun suggest(
        clothing: List<ClothingItem>,
        weather: WeatherSnapshot,
        date: LocalDate,
        variant: Int
    ): List<ClothingItem> {
        val seed = date.toEpochDay().toInt() + variant
        val target = targetWarmth(weather.tempC)
        val rainy = weather.isRainy || weather.isSnowy

        fun by(category: String) = clothing.filter { it.category == category }
        val tops = by("Tops")
        val bottoms = by("Bottoms")
        val dresses = by("Dresses")

        val useDress = dresses.isNotEmpty() &&
                (tops.isEmpty() || bottoms.isEmpty() || (target <= 2 && Math.floorMod(seed, 2) == 0))

        val result = mutableListOf<ClothingItem>()
        if (useDress) {
            pick(dresses, target, rainy, seed)?.let { result += it }
        } else {
            pick(tops, target, rainy, seed)?.let { result += it }
            pick(bottoms, target, rainy, seed)?.let { result += it }
        }
        if (target >= 3 || rainy) pick(by("Jackets"), target, rainy, seed)?.let { result += it }
        pick(by("Shoes"), target, rainy, seed)?.let { result += it }
        pick(by("Accessories"), target, rainy, seed)?.let { result += it }
        return result
    }

    fun reason(weather: WeatherSnapshot): String = when {
        weather.isSnowy -> "Snowy out there, so we've gone warm and covered."
        weather.isRainy -> "Rain is likely today, so we've added layers and sturdier pieces."
        weather.tempC >= 28 -> "It's hot, so we've kept it light and breathable."
        weather.tempC >= 24 -> "Warm day ahead, so we've picked light pieces."
        weather.tempC >= 18 -> "Mild weather, so a comfortable everyday look works."
        weather.tempC >= 12 -> "A bit cool, so we've added a warmer layer."
        else -> "Cold day, so we've picked your warmest pieces."
    }
}
