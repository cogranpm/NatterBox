package com.parinherm.audio

import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import javax.sound.sampled.*

class AudioRecognizer {

    init {
        LibVosk.setLogLevel(LogLevel.DEBUG)
    }

    /*
    fun run(){

        Model("D:\\shared\\Source\\kotlin\\resources\\vosk-model-small").use { model ->
            AudioSystem.getAudioInputStream(BufferedInputStream(FileInputStream(
                "D:\\shared\\Source\\kotlin\\resources\\vosk.wav")))
                .use { ais ->
                    Recognizer(model, 16000f).use { recognizer ->
                        var nbytes: Int
                        val b = ByteArray(4096)
                        while (ais.read(b).also { nbytes = it } >= 0) {
                            if (recognizer.acceptWaveForm(b, nbytes)) {
                                println(recognizer.result)
                            } else {
                                println(recognizer.partialResult)
                            }
                        }
                        println(recognizer.finalResult)
                    }
                }
        }
    }
     */


    suspend fun runSpeechCapture() {
        var microphone: TargetDataLine
        var speakers: SourceDataLine
        val format = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 60000f, 16, 2, 4, 44100f, false)
        val info = DataLine.Info(TargetDataLine::class.java, format)
        val dataLineInfo: DataLine.Info = DataLine.Info(SourceDataLine::class.java, format)
        speakers = AudioSystem.getLine(dataLineInfo) as SourceDataLine
        microphone = AudioSystem.getLine(info) as TargetDataLine

        try {

            Model("D:\\shared\\Source\\kotlin\\resources\\vosk-model-small").use { model ->

                Recognizer(model, 120000f).use { recognizer ->
                    microphone.open(format)
                    microphone.start()
                    speakers.open(format)
                    speakers.start()
                    captureSpeech(microphone, recognizer, speakers)
                    System.out.println(recognizer.finalResult)

                }
            }
        } catch (e: Exception) {
            println("we have stopped listening")
        } finally {
            println("cleaning up")
            speakers.drain()
            speakers.close()
            microphone.close()
        }
    }

    suspend fun captureSpeech(microphone: TargetDataLine, recognizer: Recognizer, speakers: SourceDataLine) {
        val out = ByteArrayOutputStream()
        var numBytesRead: Int
        val CHUNK_SIZE = 1024
        var bytesRead = 0

        val b = ByteArray(4096)
        val maxBytes = 100000000
        while (true) {
            yield()
            numBytesRead = microphone.read(b, 0, CHUNK_SIZE)
            bytesRead += numBytesRead
            out.write(b, 0, numBytesRead)

            //this plays back what is read, useful for caching the audio for writing to file
            //speakers.write(b, 0, numBytesRead)
            if (recognizer.acceptWaveForm(b, numBytesRead)) {
                System.out.println(recognizer.result)
            } else {
                //System.out.println(recognizer.partialResult)
            }
        }

    }


}