package com.market.trameo.core.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
    }

    val userId: Flow<String?> = context.sessionDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences[Keys.USER_ID] }

    suspend fun updateUserId(value: String) {
        context.sessionDataStore.edit { preferences ->
            preferences[Keys.USER_ID] = value
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun getCurrentUserId(): String? = userId.firstOrNull()
}

