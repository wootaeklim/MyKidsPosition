package com.wt.kids.mykidsposition.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore  by preferencesDataStore(name = "dataStore")
    private val stringKey = stringPreferencesKey("saved_place")

    // stringKey 키 값과 대응되는 값 반환
    fun getPlace() : Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[stringKey] ?: ""
        }

    suspend fun setPlace(data : String){
        context.dataStore.edit { preferences ->
            preferences[stringKey] = data
        }
    }
}