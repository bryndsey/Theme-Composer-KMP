package dev.bryanlindsey.musicgenerator3.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import dev.bryanlindsey.musicgenerator3.MidiExporter
import dev.bryanlindsey.musicgenerator3.ui.BackdropNavigationGraph
import dev.bryanlindsey.musicgenerator3.ui.BackdropNavigator
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var midiExporter: MidiExporter

    @Inject
    lateinit var backdropNavigationGraph: BackdropNavigationGraph

    @Inject
    lateinit var backdropNavigator: BackdropNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(
                backdropNavigator = backdropNavigator,
                backdropNavigationGraph = backdropNavigationGraph
            )
        }
    }
}
