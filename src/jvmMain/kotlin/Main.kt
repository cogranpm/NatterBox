// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.parinherm.audio.AudioRecognizer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.nio.file.Paths


@Composable
@Preview
fun App(job: Job, recognizerScope: CoroutineScope) {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            /*
            LaunchedEffect(key1 = Unit, block = {
                recognizer.runSpeechCapture()
            })
            val coroutineScope = rememberCoroutineScope()
            val job = coroutineScope.launch {
                recognizer.runSpeechCapture()
            }
             */
            text = "Hello, Desktop!"
            MainScope().launch {
                job.cancel()
                job.join()
                recognizerScope.cancel()
            }
        }) {
            Text(text)
        }
    }
}

fun main() = application {

    val recognizer = AudioRecognizer()
    val recognizerScope = MainScope()
    val job = recognizerScope.launch(newSingleThreadContext("recognizer-loop")) {
        recognizer.run().cancellable().onEach { value: String ->
            println("we got one: $value")
        }.catch { e -> println("Caught $e")  }.collect {}
    }

    /*
    val job = recognizer.run().onEach { value: String ->
        println("we got one: $value")
    }.catch { e -> println("Caught $e")  }.launchIn(recognizerScope)
     */

    Window(onCloseRequest = {
        shutDown(this, job, recognizerScope)
    }) {
        App(job, recognizerScope)
    }
}

fun shutDown(app: ApplicationScope, job: Job, coroutineScope: CoroutineScope) {
    val shutDownJob = MainScope().launch {
        job.cancel()
        job.join()
        coroutineScope.cancel()
        app.exitApplication()
    }
}
