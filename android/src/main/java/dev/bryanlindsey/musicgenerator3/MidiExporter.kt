package dev.bryanlindsey.musicgenerator3

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.bryanlindsey.staccato.deleteRecursive
import dev.bryanlindsey.staccato.writeCompositionToMidi
import dev.bryanlindsey.themecomposer.Composition
import java.io.File
import javax.inject.Inject

class MidiExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun exportComposition(
        savedComposition: SavedComposition
    ) {
        exportComposition(
            savedComposition.composition,
            savedComposition.name
        )
    }

    fun exportComposition(
        composition: Composition,
        fileName: String = "Composition"
    ) {
        val tempFileDirectory = context.cacheDir

        val shareableFileDirectory =
            File(tempFileDirectory, context.getString(R.string.share_directory))
        if (shareableFileDirectory.exists()) {
            deleteRecursive(shareableFileDirectory)
        }

        shareableFileDirectory.mkdirs()
        val file = writeCompositionToMidi(
            composition = composition,
            includeChords = true,
            saveDirectory = shareableFileDirectory,
            fileName = "$fileName.mid"
        )
        shareFile(file)
    }

    private fun shareFile(file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            context.getString(R.string.file_provider_authority),
            file
        )
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "audio/midi"
        intent.addCategory(Intent.CATEGORY_DEFAULT)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val chooserIntent = Intent.createChooser(intent, "Export MIDI File")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooserIntent)
    }
}
