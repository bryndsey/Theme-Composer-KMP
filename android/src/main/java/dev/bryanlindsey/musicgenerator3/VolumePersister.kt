package dev.bryanlindsey.musicgenerator3

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import dev.bryanlindsey.musicgenerator3.player.Voice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val voiceToVolumePreferenceKeyMap = Voice.values().map {
    val prefKey = floatPreferencesKey("${it.name}_volume")
    it to prefKey
}.toMap()

private val voiceToMutedPreferenceKeyMap = Voice.values().map {
    val prefKey = booleanPreferencesKey("${it.name}_muted")
    it to prefKey
}.toMap()

@Singleton
class VolumePersister @Inject constructor(private val dataStore: DataStore<Preferences>) {
    // Not sure if unconfined is the best idea or not
    private val coroutineScope = CoroutineScope(Dispatchers.Unconfined)

    fun getStoredVolumeFlow(voice: Voice): Flow<Float> {
        val prefKey = voiceToVolumePreferenceKeyMap[voice]
        return dataStore.data.map { preferences ->
            prefKey?.let{ preferences[prefKey] } ?: voice.defaultVolume
        }
    }

    fun getStoredVolume(voice: Voice): Float = runBlocking { getStoredVolumeFlow(voice).first() }

    fun updateStoredVolume(voice: Voice, newVolume: Float) {
        val prefKey = voiceToVolumePreferenceKeyMap[voice] ?: return

        coroutineScope.launch {
            dataStore.edit {
                it[prefKey] = newVolume
            }
        }
    }

    fun getStoredMutedStateFlow(voice: Voice): Flow<Boolean> {
        val prefKey = voiceToMutedPreferenceKeyMap[voice]
        return dataStore.data.map { preferences ->
            prefKey?.let{ preferences[prefKey] } ?: voice.defaultMutedState
        }
    }

    fun getStoredMutedState(voice: Voice) = runBlocking { getStoredMutedStateFlow(voice).first() }

    fun updateStoredMutedState(voice: Voice, muted: Boolean) {
        val prefKey = voiceToMutedPreferenceKeyMap[voice] ?: return

        coroutineScope.launch {
            dataStore.edit {
                it[prefKey] = muted
            }
        }
    }
}

