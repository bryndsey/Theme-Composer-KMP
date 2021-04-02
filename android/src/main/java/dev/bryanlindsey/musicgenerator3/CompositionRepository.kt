package dev.bryanlindsey.musicgenerator3

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import dev.bryanlindsey.staccato.*
import dev.bryanlindsey.themecomposer.MidiInstrument
import dev.bryanlindsey.themecomposer.structure.Scale
import devbryanlindseymusicgenerator3data.Composition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompositionRepository @Inject constructor(private val database: Database) {

    suspend fun saveComposition(
        composition: dev.bryanlindsey.themecomposer.Composition,
        name: String?,
    ) {
        withContext(Dispatchers.IO) {
            val nextCompositionNumber = (getAllCompositions().maxOfOrNull { it.id } ?: 0) + 1

            val databaseComposition = Composition(
                nextCompositionNumber,
                name ?: "Composition $nextCompositionNumber",
                composition.timeSignature,
                composition.tempo,
                composition.scale.rootPitch,
                composition.scale.type,
                composition.getMelodyStaccato(),
                composition.getChordStaccato(),
                composition.getMetronomeStaccato(),
                composition.startBeat,
                composition.melodyInstrument,
                composition.chordInstrument
            )

            database.compositionQueries.insert(databaseComposition)
        }
    }

    private suspend fun getAllCompositions(): List<SavedComposition> {
         return withContext(Dispatchers.IO) {
             database.compositionQueries.selectAll().executeAsList()
                 .map {
                     SavedComposition(
                         it.id,
                         it.name,
                         it.toDomain()
                     )
                 }
         }
    }

    fun getAllCompositionsFlow(): Flow<List<SavedComposition>> {
        return database.compositionQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                list.map {
                    SavedComposition(
                        it.id,
                        it.name,
                        it.toDomain()
                    )
                }
            }
    }

    suspend fun updateComposition(updatedComposition: SavedComposition) {
        withContext(Dispatchers.IO) {
            database.compositionQueries.updateName(updatedComposition.name, updatedComposition.id)
        }
    }

    suspend fun deleteComposition(savedComposition: SavedComposition) {
        withContext(Dispatchers.IO) {
            database.compositionQueries.delete(savedComposition.id)
        }
    }
}

data class SavedComposition(
    val id: Long,
    val name: String,
    val composition: dev.bryanlindsey.themecomposer.Composition
)

fun Composition.toDomain() =
    dev.bryanlindsey.themecomposer.Composition(
        tempo = tempo,
        timeSignature = time_signature,
        scale = Scale(scale_pitch, scale_type),
        chordProgression = staccatoChordsToChordProgression(chord_staccato_string),
        melody = staccatoMelodyToNotes(melody_staccato_string),
        // TODO: Change defaults, or maybe make not nullable
        melodyInstrument = melody_instrument ?: MidiInstrument.Steel_String_Guitar,
        chordInstrument = chord_instrument ?: MidiInstrument.Piano,
        startBeat = start_beat ?: 0,
    )
