package com.example.prjmetrix

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.wardrobeDataStore by preferencesDataStore(
    name = "wardrobe_preferences"
)

class WardrobePreferences(private val context: Context) {

    private val wallpaperKey =
        stringPreferencesKey("wallpaper_uri")

    val wallpaper: Flow<String?> =
        context.wardrobeDataStore.data.map { preferences ->
            preferences[wallpaperKey]
        }

    suspend fun setWallpaper(uri: String) {
        context.wardrobeDataStore.edit { preferences ->
            preferences[wallpaperKey] = uri
        }
    }
}