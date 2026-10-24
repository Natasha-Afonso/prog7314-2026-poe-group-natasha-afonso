package com.example.prjmetrix

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

//(Shreyas, 2020)
//local db metrix_auth tied to application context
private val Context.dataStore by preferencesDataStore(name = "metrix_auth")

class Token(private val context: Context) {

    //constant strings to save data points in db
    companion object {
        private val ID_TOKEN = stringPreferencesKey("id_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val UID = stringPreferencesKey("uid")
        private val EMAIL = stringPreferencesKey("email")
    }

    //saved the data received from the authResponse
    suspend fun save(uid: String, email: String, idToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[UID] = uid
            prefs[EMAIL] = email
            prefs[ID_TOKEN] = idToken
            prefs[REFRESH_TOKEN] = refreshToken
        }
    }

    //gets the token ID for authorization
    suspend fun getIdToken(): String? = context.dataStore.data.first()[ID_TOKEN]

    //when person is logged in = true
    suspend fun isLoggedIn(): Boolean = !getIdToken().isNullOrBlank()

    suspend fun getUid(): String? = context.dataStore.data.first()[UID]

    suspend fun clear() { //data is cleared when user logsout
        context.dataStore.edit { it.clear() }
    }
}

/*References
*
* Simplified Coding, 2021. #12 Android Login/Signup with MVVM - user Logout [video online] [source code]. Available at: < https://youtu.be/q_5xrPYRoAg?si=gRbHfP1TazLTqbrY > [Accessed 19 September 2026
* Shreyas, P., 2020. Data Store Example. (Version 10.0) [source code]. Available at: < https://github.com/PatilShreyas/DataStoreExample/blob/master/app/src/main/java/dev/shreyaspatil/datastore/example/preference/SettingsManager.kt > [Accessed 19 September 2026]
* */