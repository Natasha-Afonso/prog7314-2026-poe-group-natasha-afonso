package com.example.prjmetrix

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "metrix_auth")

class Token(private val context: Context) {

    companion object {
        private val ID_TOKEN = stringPreferencesKey("id_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val UID = stringPreferencesKey("uid")
        private val EMAIL = stringPreferencesKey("email")
    }

    suspend fun save(uid: String, email: String, idToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[UID] = uid
            prefs[EMAIL] = email
            prefs[ID_TOKEN] = idToken
            prefs[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun getIdToken(): String? = context.dataStore.data.first()[ID_TOKEN]

    suspend fun isLoggedIn(): Boolean = !getIdToken().isNullOrBlank()

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}